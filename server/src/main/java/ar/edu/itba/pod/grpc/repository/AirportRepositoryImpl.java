package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;

import java.util.*;

public class AirportRepositoryImpl implements AirportRepository {

    private static AirportRepositoryImpl instance;

    private Integer counterId = 1;
    private final List<Counter> totalCounters = new ArrayList<>();

    private final Map<Sector,List<Counter>> countersBySector = new HashMap<>();

    private final Map<Sector,List<ContiguousRange>> ranges = new HashMap<>();

    private final Map<Sector,List<AssignedRange>> onGoingAirlineRange = new HashMap<>();

    private final Map<Sector, Queue<AssignedRange>> pendingAirlineRange = new HashMap<>();


    private AirportRepositoryImpl() {
    }

    public synchronized static AirportRepositoryImpl getInstance() {
        if(instance == null) {
            instance = new AirportRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addSector(String name) {
        Sector sectorToAdd = new Sector(name);
        // ---- casos de error ----
        if(countersBySector.containsKey(sectorToAdd)) {
            throw new IllegalArgumentException("Ya existe un sector con el nombre indicado");
        }
        // ---- fin de casos de error ----

        // agrego el sector a todos las colecciones necesarias
        countersBySector.put(sectorToAdd, new ArrayList<>());
        ranges.put(sectorToAdd, new ArrayList<>());
        onGoingAirlineRange.put(sectorToAdd, new ArrayList<>());
        pendingAirlineRange.put(sectorToAdd, new LinkedList<>());
    }

    @Override
    public synchronized ContiguousRange addCountersToSector(String sectorName, int count) {
        Sector sector = new Sector(sectorName);

        // ---- casos de error ----
        if(!containsSector(sector)) {
            throw new IllegalArgumentException("No existe un sector con el nombre indicado");
        }
        if(count < 0) {
            throw new IllegalArgumentException("La cantidad de counters a agregar debe ser mayor a 0");
        }
        // ---- fin de casos de error ----

        int trueCount = count;
        int newCounterId = counterId;
        int occupiedCounters = 0;
        List<Counter> currentCounters = null;

        // caso en que ya existe un rango contiguo en el sector y el último mostrador es el anterior al que quiero quiere agregar -> los fusiono
        if(ranges.containsKey(sector) && !ranges.get(sector).isEmpty()) {
            ContiguousRange oldRange = ranges.get(sector).get(ranges.get(sector).size() - 1);
            // es el ultimo actual el anterior al que voy a agregar?
            if(oldRange.getEnd() == counterId - 1) {
                occupiedCounters = oldRange.getOccupied();
                currentCounters = oldRange.getCounters();
                // saco ese último rango para después fusionarlo con el nuevo
                ranges.get(sector).remove(oldRange);
                newCounterId = oldRange.getStart();
                count += oldRange.getEnd() - oldRange.getStart() + 1;
            }
        }

        // creo el nuevo rango continuo que se va a agregar
        ContiguousRange contiguousRange = new ContiguousRange(newCounterId, newCounterId + count - 1);
        contiguousRange.occupy(occupiedCounters);
        contiguousRange.addAll(currentCounters);
        ranges.get(sector).add(contiguousRange);

        //agrego el rango contiguo a la colección necesaria
        for(int i = 0 ; i < trueCount ; i++) {

            // agrego los nuevos mostradores a todas las colecciones necesarias
            Counter counterToAdd = new Counter(counterId, CounterStatus.PENDING);
            contiguousRange.add(counterToAdd);
            totalCounters.add(counterToAdd);
            countersBySector.get(sector).add(counterToAdd);
            counterId++;
        }
        return contiguousRange;
    }

    @Override
    public synchronized Map<Sector, List<Counter>> getSectors() {
        return countersBySector;
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
    public synchronized void assignCounterRangeToAirline(String sectorName, List<String> flightNames, String airlineName, int count) {
        Sector sector = new Sector(sectorName);
        Airline airline = new Airline(airlineName);

        // ---- casos de error ----
        if(!containsSector(sector)) {
            throw new IllegalArgumentException();
        }
        List<Flight> flights = new ArrayList<>();
        for(String name : flightNames) {
            Flight flight = new Flight(airline, name);
            flights.add(flight);
        }
        boolean hasExpectedPassengers = false;
        Map<Booking, Flight> expectedPassengers = PassengerRepositoryImpl.getInstance().getExpectedPassengers();
        for(Booking booking : expectedPassengers.keySet()) {
            if(flights.contains(expectedPassengers.get(booking))) {
                flights.remove(expectedPassengers.get(booking));
                hasExpectedPassengers = true;
                if(!expectedPassengers.get(booking).getAirline().equals(airline)) {
                    throw new IllegalArgumentException("El vuelo no pertenece a la aerolinea indicada");
                }
                continue;
            }
            hasExpectedPassengers = false;
        }
        if(!hasExpectedPassengers) {
            throw new IllegalArgumentException("No se encontraron pasajeros esperados para al menos uno de los vuelos indicados");
        }
        for(AssignedRange assignedRange : onGoingAirlineRange.get(sector)) {
            if(assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Ya existe un rango asignado para al menos uno de los vuelos indicados");
                    }
                }
            }
        }
        for(AssignedRange assignedRange : pendingAirlineRange.get(sector)) {
            if(assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Ya existe un rango pendiente para al menos uno de los vuelos indicados");
                    }
                }
            }
        }
        Map<Airline, CheckIn> airlineCheckIns = HistoryRepositoryImpl.getInstance().getAirlineCheckInHistory();
        for(CheckIn checkIn : airlineCheckIns.values()) {
            if(checkIn.getAirline().equals(airline)) {
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
        for(ContiguousRange range : contiguousRangeList) {
            int totalCounters = range.getCounters().size();
            if((totalCounters - range.getOccupied()) >= count) {
                List<Counter> countersToAdd = new ArrayList<>();
                int counterCount = 0;
                // checkeo la lista de contadores de cada rango contiguo que tenga espacio suficiente en su totalidad
                for(Counter counter : range.getCounters()) {
                    // Si el mostrador esta pending significa que esta libre para ser asignado
                    if(counter.getStatus() == CounterStatus.PENDING) {
                        countersToAdd.add(counter);
                        counterCount++;
                    }
                    // si no hay espacio contiguo (mostradores pending), se limpia la lista y se resetea el contador
                    else {
                        countersToAdd.clear();
                        counterCount = 0;
                    }
                    if(counterCount == count) {
                        break;
                    }
                }
                // si la cantidad de contadores a agregar es igual a la cantidad de contadores necesarios, se cambia el estado de los contadores y se agrega el rango asignado
                if(countersToAdd.size() == count) {
                    countersToAdd.forEach(counter -> counter.setStatus(CounterStatus.READY));
                    finishSetupOfAssignedRange( count, airline, countersToAdd, sector, flights);
                    range.occupy(count);
                    return;
                }

            }
        }
    }

    // crea el rango asignado a la aerolinea y lo agrega a la colección a la que pertenece
    private void finishSetupOfAssignedRange(int count, Airline airline, List<Counter> countersToAdd, Sector sector, List<Flight> flights) {
        AssignedRange assignedRange = new AssignedRange(countersToAdd.get(0).getNumber(), countersToAdd.get(countersToAdd.size() - 1).getNumber() , airline, count);
        assignedRange.getCounters().addAll(countersToAdd);
        assignedRange.getFlights().addAll(flights);
        onGoingAirlineRange.get(sector).add(assignedRange);
    }

    @Override
    public void freeAssignedRange(String sectorName, int rangeId, String airlineName) {
        Sector sector = new Sector(sectorName);

        // ---- casos de error ----
        if(!containsSector(sector)) {
            throw new IllegalArgumentException("No existe un sector con el nombre indicado");
        }
        // ---- fin de casos de error ----

        Airline airline = new Airline(airlineName);
        Optional<AssignedRange> rangeToFree = searchAssignedRangeForAirline(onGoingAirlineRange.get(sector), rangeId, airline);
        if(rangeToFree.isPresent()) {
            rangeToFree.get().getCounters().forEach(counter -> counter.setStatus(CounterStatus.PENDING));
            onGoingAirlineRange.get(sector).remove(rangeToFree.get());
            for (ContiguousRange contiguousRange : ranges.get(sector)) {
                if (contiguousRange.getStart() <= rangeId && contiguousRange.getEnd() >= rangeId) {
                    contiguousRange.occupy(-rangeToFree.get().getTotalCounters());
                }
            }
        }
        else {
            throw new IllegalArgumentException("No se encontró un rango asignado al que pertenezca el numero de rango indicado");
        }
    }

    @Override
    public synchronized Queue<AssignedRange> getpendingAirlineRange(String sectorName) {
        Sector sector = new Sector(sectorName);
        if(!containsSector(sector)) {
            throw new IllegalArgumentException("No existe un sector con el nombre indicado");
        }
        return pendingAirlineRange.get(sector);
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        for(AssignedRange assignedRange : assignedRanges) {
            if(assignedRange.getStart() <= rangeId && assignedRange.getEnd() >= rangeId && airline.equals(assignedRange.getAirline())) {
                return Optional.of(assignedRange);
            }
        }
        return Optional.empty();
    }
    @Override
    public boolean containsSector(Sector sector) {
        return countersBySector.containsKey(sector);
    }
}
