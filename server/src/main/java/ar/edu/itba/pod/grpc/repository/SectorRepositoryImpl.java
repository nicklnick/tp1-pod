package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.CheckInServiceImpl;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.CheckInService;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.*;
import java.util.stream.IntStream;

public class SectorRepositoryImpl implements SectorRepository {

    private static SectorRepositoryImpl instance;
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final CheckInService checkInService = new CheckInServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();

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
        return Map.copyOf(countersBySector);
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
        final ContiguousRange contiguousRange = new ContiguousRange(newCounterId, newCounterId + count - 1, sector);
        contiguousRange.occupy(occupiedCounters);
        contiguousRange.addAll(currentCounters);
        ranges.get(sector).add(contiguousRange);

        // add the new counters
        for (int i = 0; i < trueCount; i++) {
            Counter counterToAdd = new Counter(counterId, CounterStatus.PENDING_ASSIGNATION);
            contiguousRange.add(counterToAdd);
            totalCounters.add(counterToAdd);
            countersBySector.get(sector).add(counterToAdd);
            counterId++;
        }

        assignPendingRanges(sector);

        return contiguousRange;
    }

    @Override
    public Map<Sector, List<ContiguousRange>> getContiguousRanges() {
        return Map.copyOf(ranges);
    }

    @Override
    public List<ContiguousRange> getContiguosRangesBySector(Sector sector) {
        return new ArrayList<>(ranges.getOrDefault(sector,new ArrayList<>()));
    }

    @Override
    public Map<Sector, List<AssignedRange>> getOnGoingAirlineRange() {
        return Map.copyOf(onGoingAirlineRange);
    }

    @Override
    public List<AssignedRange> getOnGoingAirlineRangeBySector(Sector sector) {
            return new ArrayList<>(onGoingAirlineRange.getOrDefault(sector, new ArrayList<>()));
    }

    @Override
    public synchronized Queue<AssignedRange> getPendingAirlineRange(Sector sector) {
        return pendingAirlineRange.get(sector);
    }

    @Override
    public Optional<AssignedRange> freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        final Optional<AssignedRange> rangeToFree = searchAssignedRangeForAirline(onGoingAirlineRange.get(sector), rangeId, airline);
        if(rangeToFree.isEmpty())
            throw new IllegalArgumentException("Assigned range not found for given airline");

        rangeToFree.get().getCounters().forEach(counter -> counter.setStatus(CounterStatus.PENDING_ASSIGNATION));
        onGoingAirlineRange.get(sector).remove(rangeToFree.get());
        for (ContiguousRange contiguousRange : ranges.get(sector)) {
            if (contiguousRange.getStart() <= rangeId && contiguousRange.getEnd() >= rangeId) {
                contiguousRange.occupy(-rangeToFree.get().getTotalCounters());
            }
        }
        //luego de liberar se tiene que asignar pendientes
        assignPendingRanges(sector);
        return rangeToFree;
    }

    @Override
    public synchronized Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {


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
                    if (counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
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
                // si la cantidad de contadores a agregar es igual a la cantidad de contadores necesarios,
                // se cambia el estado de los contadores y se agrega el rango asignado
                if (countersToAdd.size() == count) {
                    countersToAdd.forEach(counter -> counter.setStatus(CounterStatus.READY_FOR_CHECKIN));
                    final AssignedRange result = finishSetupOfAssignedRange(count, airline, countersToAdd, sector, flights);
                    range.occupy(count);

                    return Optional.of(result);
                }
            }
        }

        // si no hay espacio contiguo, se crea un rango pendiente
        pendingAirlineRange.get(sector).add(new AssignedRange(sector, airline, count));

        return Optional.empty();
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

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirlineBySector(int rangeId, Airline airline, Sector sector){
        List<AssignedRange> assignedRanges = onGoingAirlineRange.get(sector);
        return searchAssignedRangeForAirline(assignedRanges,rangeId,airline);
    }
    @Override
    public Map<Sector, List<AssignedRange>> listCounters() {
        final Map<Sector, List<AssignedRange>> result = new HashMap<>();
        // Recorro los sectores y agrego los rangos asignados a la aerolinea
        for(Sector sector : onGoingAirlineRange.keySet()) {
            result.put(sector, new ArrayList<>(onGoingAirlineRange.get(sector)));
        }

        // Recorro los sectores y agrego los counters no asignados
        for(Sector sector : countersBySector.keySet()) {
            final List<Counter> unassignedCounters = new LinkedList<>();
            for(Counter counter : countersBySector.get(sector)) {
                if(counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
                    unassignedCounters.add(counter);
                }
            }

            if(!unassignedCounters.isEmpty()) {
                final List<AssignedRange> unassignedRanges = obtainRanges(unassignedCounters);
                result.put(sector, unassignedRanges);
            }
        }

        return result;
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters(Sector sector) {
        final Map<Sector, List<AssignedRange>> result = new HashMap<>();
        result.put(sector, new ArrayList<>(onGoingAirlineRange.get(sector)));

        final List<Counter> unassignedCounters = new LinkedList<>();
        for(Counter counter : countersBySector.get(sector)) {
            if(counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
                unassignedCounters.add(counter);
            }
        }

        if(!unassignedCounters.isEmpty()) {
            final List<AssignedRange> unassignedRanges = obtainRanges(unassignedCounters);
            result.put(sector, unassignedRanges);
        }

        return result;
    }

    @Override
    public int getPendingAssignmentsAheadOf(Sector sector, AssignedRange range) {
        final Queue<AssignedRange> queue = pendingAirlineRange.get(sector);

        return IntStream.range(0, queue.size())
                .filter(i -> queue.toArray()[i].equals(range))
                .findFirst()
                .orElse(-1);
    }

    @Override
    public boolean airportContainsAtLeastOneCounter() {
        return !countersBySector.isEmpty();
    }


    // Given a Counter list, it returns a list of Ranges
    // Eg. [1, 2, 3, 5, 6, 7, 8] -> [1-3, 5-8]
    private List<AssignedRange> obtainRanges(List<Counter> countersToAdd) {
        final List<AssignedRange> result = new LinkedList<>();

        for(int i = 0; i < countersToAdd.size(); i++) {

            final int start = countersToAdd.get(i).getNumber();

            while(i < countersToAdd.size() - 1 && countersToAdd.get(i).getNumber() + 1 == countersToAdd.get(i + 1).getNumber()) {
                i++;
            }
            if(i == countersToAdd.size() - 1) {
                final int end = countersToAdd.get(i).getNumber();
                final AssignedRange assignedRange = new AssignedRange(start, end, null, null,end - start + 1);
                result.add(assignedRange);

                break;
            }

            int end = countersToAdd.get(i).getNumber();

            final AssignedRange assignedRange = new AssignedRange(start, end, null, null, end - start + 1);
            result.add(assignedRange);
        }

        return result;
    }

    private AssignedRange getAssignedRange(int rangeId, Sector sector) {
        for (AssignedRange assignedRange : onGoingAirlineRange.get(sector)) {
            if (assignedRange.getStart() <= rangeId && assignedRange.getEnd() >= rangeId) {
                return assignedRange;
            }
        }
        return null;
    }

    private AssignedRange finishSetupOfAssignedRange(int count, Airline airline, List<Counter> countersToAdd, Sector sector, List<Flight> flights) {
        final AssignedRange assignedRange = new AssignedRange(countersToAdd.get(0).getNumber(), countersToAdd.get(countersToAdd.size() - 1).getNumber(), sector, airline, count);
        assignedRange.getCounters().addAll(countersToAdd);
        assignedRange.getFlights().addAll(flights);
        for(Flight flight : flights) {
            checkInService.addAvailableRangeForFlight(flight, assignedRange);
        }
        historyService.addAssignedRange(assignedRange);
        onGoingAirlineRange.get(sector).add(assignedRange);

        return assignedRange;
    }

    private void assignPendingRanges(Sector sector) {
        if(!pendingAirlineRange.get(sector).isEmpty()) {
            for(AssignedRange pendingRange: pendingAirlineRange.get(sector)) {
                Optional<AssignedRange> assigned = assignCounterRangeToAirline(pendingRange.getSector(), pendingRange.getAirline(), pendingRange.getFlights(), pendingRange.getTotalCounters());
                if(assigned.isPresent()){
                    pendingAirlineRange.get(sector).remove(pendingRange);
                    break;
                }
            }
        }
    }
}
