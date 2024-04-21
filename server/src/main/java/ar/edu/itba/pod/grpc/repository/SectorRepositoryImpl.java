package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.*;

public class SectorRepositoryImpl implements SectorRepository {

    private static SectorRepositoryImpl instance;

    private final PassengerService passengerService = new PassengerServiceImpl();

    private final List<Counter> totalCounters = new ArrayList<>();
    private final Map<Sector, List<Counter>> countersBySector = new HashMap<>();
    private final Map<Sector, List<ContiguousRange>> ranges = new HashMap<>();
    private final Map<Sector, List<AssignedRange>> onGoingAirlineRange = new HashMap<>();
    private final Map<Sector, Queue<AssignedRange>> pendingAirlineRange = new HashMap<>();
    private Integer counterId = 1;

    private SectorRepositoryImpl() {
    }

    public synchronized static SectorRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new SectorRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addSector(Sector sector) {
        countersBySector.put(sector, new ArrayList<>());
        ranges.put(sector, new ArrayList<>());
        onGoingAirlineRange.put(sector, new ArrayList<>());
        pendingAirlineRange.put(sector, new LinkedList<>());
    }

    @Override
    public synchronized Map<Sector, List<Counter>> listSectors() {
        return countersBySector;
    }

    @Override
    public boolean containsSector(Sector sector) {
        return countersBySector.containsKey(sector);
    }

    @Override
    public synchronized ContiguousRange addCountersToSector(Sector sector, int count) {
        int trueCount = count;
        int newCounterId = counterId;
        int occupiedCounters = 0;
        List<Counter> currentCounters = null;

        // check if there is a range that ends in the previous counter
        if (ranges.containsKey(sector) && !ranges.get(sector).isEmpty()) {
            ContiguousRange oldRange = ranges.get(sector).get(ranges.get(sector).size() - 1);
            if (oldRange.getEnd() == counterId - 1) {
                occupiedCounters = oldRange.getOccupied();
                currentCounters = oldRange.getCounters();
                ranges.get(sector).remove(oldRange);
                newCounterId = oldRange.getStart();
                count += oldRange.getEnd() - oldRange.getStart() + 1;
            }
        }

        // create the new range
        final ContiguousRange contiguousRange = new ContiguousRange(newCounterId, newCounterId + count - 1);
        contiguousRange.occupy(occupiedCounters);
        contiguousRange.addAll(currentCounters);
        ranges.get(sector).add(contiguousRange);

        // add the new counters
        for (int i = 0; i < trueCount; i++) {
            Counter counterToAdd = new Counter(counterId, CounterStatus.PENDING);
            contiguousRange.add(counterToAdd);
            totalCounters.add(counterToAdd);
            countersBySector.get(sector).add(counterToAdd);
            counterId++;
        }

        return contiguousRange;
    }

    @Override
    public Map<Sector, List<ContiguousRange>> getContiguousRanges() {
        return ranges;
    }

    @Override
    public Map<Sector, List<AssignedRange>> getOnGoingAirlineRange() {
        return onGoingAirlineRange;
    }

    @Override
    public synchronized Queue<AssignedRange> getPendingAirlineRange(Sector sector) {
        return pendingAirlineRange.get(sector);
    }

    @Override
    public void freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        final Optional<AssignedRange> rangeToFree = searchAssignedRangeForAirline(onGoingAirlineRange.get(sector), rangeId, airline);
        if(rangeToFree.isEmpty())
            throw new IllegalArgumentException("No se encontro el rango asignado para la aerolinea indicada");

        rangeToFree.get().getCounters().forEach(counter -> counter.setStatus(CounterStatus.PENDING));
        onGoingAirlineRange.get(sector).remove(rangeToFree.get());
        for (ContiguousRange contiguousRange : ranges.get(sector)) {
            if (contiguousRange.getStart() <= rangeId && contiguousRange.getEnd() >= rangeId) {
                contiguousRange.occupy(-rangeToFree.get().getTotalCounters());
            }
        }
    }

    @Override
    public synchronized void assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        // ---- casos de error ----
        // TODO: sacar estos chequeos y moverlos al service
        boolean hasExpectedPassengers = false;
        Map<Booking, Flight> expectedPassengers = passengerService.listExpectedPassengers();

        // chequeo si los vuelos indicados tienen pasajeros esperados
        for (Booking booking : expectedPassengers.keySet()) {
            if (flights.contains(expectedPassengers.get(booking))) {
                flights.remove(expectedPassengers.get(booking));
                hasExpectedPassengers = true;
                if (!expectedPassengers.get(booking).getAirline().equals(airline)) {
                    throw new IllegalArgumentException("El vuelo no pertenece a la aerolinea indicada");
                }
                continue;
            }
            hasExpectedPassengers = false;
        }

        // si no hay pasajeros esperados para al menos uno de los vuelos indicados, se lanza una excepción
        if (!hasExpectedPassengers) {
            throw new IllegalArgumentException("No se encontraron pasajeros esperados para al menos uno de los vuelos indicados");
        }

        // chequeo si la aerolinea ya tiene un rango asignado o pendiente
        for (AssignedRange assignedRange : onGoingAirlineRange.get(sector)) {
            if (assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Ya existe un rango asignado para al menos uno de los vuelos indicados");
                    }
                }
            }
        }

        // chequeo si la aerolinea ya tiene un rango pendiente
        for (AssignedRange assignedRange : pendingAirlineRange.get(sector)) {
            if (assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Ya existe un rango pendiente para al menos uno de los vuelos indicados");
                    }
                }
            }
        }
        // chequeo si la aerolinea ya tiene un check-in iniciado
        // TODO: sacar el repo y poner el service cuando esté hecho
        Map<Airline, CheckIn> airlineCheckIns = HistoryRepositoryImpl.getInstance().getAirlineCheckInHistory();
        for (CheckIn checkIn : airlineCheckIns.values()) {
            if (checkIn.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (checkIn.getFlight().equals(flight)) {
                        throw new IllegalArgumentException("No se puede iniciar el check-in de un vuelo dos o mas veces");
                    }
                }
            }
        }
        // ---- fin de casos de error ----

        // quiero ver si en rangos contiguos del sector si hay espacio contiguo para una aerolinea
        List<ContiguousRange> contiguousRangeList = ranges.get(sector);
        // chequeo cada rango contiguo para ver si tiene lugar
        for (ContiguousRange range : contiguousRangeList) {
            int totalCounters = range.getCounters().size();
            if ((totalCounters - range.getOccupied()) >= count) {
                List<Counter> countersToAdd = new ArrayList<>();
                int counterCount = 0;
                // checkeo la lista de contadores de cada rango contiguo que tenga espacio suficiente en su totalidad
                for (Counter counter : range.getCounters()) {
                    // Si el mostrador esta pending significa que esta libre para ser asignado
                    if (counter.getStatus() == CounterStatus.PENDING) {
                        countersToAdd.add(counter);
                        counterCount++;
                    }
                    // si no hay espacio contiguo (mostradores pending), se limpia la lista y se resetea el contador
                    else {
                        countersToAdd.clear();
                        counterCount = 0;
                    }
                    if (counterCount == count) {
                        break;
                    }
                }
                // si la cantidad de contadores a agregar es igual a la cantidad de contadores necesarios, se cambia el estado de los contadores y se agrega el rango asignado
                if (countersToAdd.size() == count) {
                    countersToAdd.forEach(counter -> counter.setStatus(CounterStatus.READY));
                    finishSetupOfAssignedRange(count, airline, countersToAdd, sector, flights);
                    range.occupy(count);
                    return;
                }

            }
        }
    }

    // TODO: que onda esto? no usa nada de la clase repository
    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        for (AssignedRange assignedRange : assignedRanges) {
            if (assignedRange.getStart() <= rangeId && assignedRange.getEnd() >= rangeId && airline.equals(assignedRange.getAirline())) {
                return Optional.of(assignedRange);
            }
        }
        return Optional.empty();
    }

    // crea el rango asignado a la aerolinea y lo agrega a la colección a la que pertenece
    private void finishSetupOfAssignedRange(int count, Airline airline, List<Counter> countersToAdd, Sector sector, List<Flight> flights) {
        final AssignedRange assignedRange = new AssignedRange(countersToAdd.get(0).getNumber(), countersToAdd.get(countersToAdd.size() - 1).getNumber(), airline, count);
        assignedRange.getCounters().addAll(countersToAdd);
        assignedRange.getFlights().addAll(flights);
        onGoingAirlineRange.get(sector).add(assignedRange);
    }
}
