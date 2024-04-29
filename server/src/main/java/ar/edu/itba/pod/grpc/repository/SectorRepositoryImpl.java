package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.CheckInServiceImpl;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.NotificationsServiceImpl;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.CheckInService;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class SectorRepositoryImpl implements SectorRepository {

    private static SectorRepositoryImpl instance;
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final CheckInService checkInService = new CheckInServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();
    private final NotificationsService notificationsService = new NotificationsServiceImpl();

    private final List<Counter> totalCounters = new ArrayList<>();
    private final Map<Sector, List<Counter>> countersBySector = new HashMap<>();
    private final Map<Sector, List<ContiguousRange>> ranges = new HashMap<>();
    private final Map<Sector, List<AssignedRange>> onGoingAirlineRange = new HashMap<>();
    private final Map<Sector, Queue<AssignedRange>> pendingAirlineRange = new HashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
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
    public void addSector(Sector sector) {
        readWriteLock.writeLock().lock();
        try {
            countersBySector.put(sector, new ArrayList<>());
            ranges.put(sector, new ArrayList<>());
            onGoingAirlineRange.put(sector, new ArrayList<>());
            pendingAirlineRange.put(sector, new LinkedList<>());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public synchronized Map<Sector, List<Counter>> listSectors() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(countersBySector);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsSector(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return countersBySector.containsKey(sector);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public synchronized ContiguousRange addCountersToSector(Sector sector, int count) {
        ContiguousRange contiguousRange;

        readWriteLock.writeLock().lock();
        try {
            int trueCount = count;
            int newCounterId = counterId;
            int occupiedCounters = 0;
            List<Counter> currentCounters = null;

            // Check if there is a range that ends in the previous counter
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

            // Create the new range
            contiguousRange = new ContiguousRange(newCounterId, newCounterId + count - 1, sector);
            contiguousRange.occupy(occupiedCounters);
            contiguousRange.addAll(currentCounters);
            ranges.get(sector).add(contiguousRange);

            // Add the new counters
            for (int i = 0; i < trueCount; i++) {
                Counter counterToAdd = new Counter(counterId, CounterStatus.PENDING_ASSIGNATION);
                contiguousRange.add(counterToAdd);
                totalCounters.add(counterToAdd);
                countersBySector.get(sector).add(counterToAdd);
                counterId++;
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }

        Optional<AssignedRange> result = assignPendingRanges(sector);
        if (result.isPresent() && notificationsService.isRegisteredForNotifications(result.get().getAirline())) {
            NotificationData notification = NotificationData.newBuilder()
                    .setType(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING_CHANGED)
                    .setCounterRange(result.get())
                    .setSector(sector)
                    .setFlights(result.get().getFlights())
                    .setPendingsAhead(getPendingAssignmentsAheadOf(sector, result.get()))
                    .build();

            notificationsService.sendNotification(notification);
        }

        return contiguousRange;
    }

    @Override
    public Map<Sector, List<ContiguousRange>> getContiguousRanges() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(ranges);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<ContiguousRange> getContiguosRangesBySector(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(ranges.getOrDefault(sector, new ArrayList<>()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<Sector, List<AssignedRange>> getOnGoingAirlineRange() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(onGoingAirlineRange);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<AssignedRange> getOnGoingAirlineRangeBySector(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(onGoingAirlineRange.getOrDefault(sector, new ArrayList<>()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Queue<AssignedRange> getPendingAirlineRange(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return pendingAirlineRange.get(sector);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AssignedRange> freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        final Optional<AssignedRange> rangeToFree = searchAssignedRangeForAirline(onGoingAirlineRange.get(sector), rangeId, airline);

        if (rangeToFree.isEmpty())
            throw new IllegalArgumentException("Assigned range not found for given airline");

        readWriteLock.writeLock().lock();
        try {
            rangeToFree.get().getCountersMap().keySet().forEach(counter -> counter.setStatus(CounterStatus.PENDING_ASSIGNATION));
            onGoingAirlineRange.get(sector).remove(rangeToFree.get());
            for (ContiguousRange contiguousRange : ranges.get(sector)) {
                if (contiguousRange.getStart() <= rangeId && contiguousRange.getEnd() >= rangeId) {
                    contiguousRange.occupy(-rangeToFree.get().getTotalCounters());
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }

        if (notificationsService.isRegisteredForNotifications(airline)) {
            NotificationData freedRangeNotification = NotificationData.newBuilder()
                    .setType(NotificationType.NOTIFICATION_DISMISSED_COUNTERS)
                    .setSector(sector)
                    .setFlights(rangeToFree.get().getFlights())
                    .setPendingsAhead(getPendingAssignmentsAheadOf(sector, rangeToFree.get()))
                    .build();

            notificationsService.sendNotification(freedRangeNotification);

            // Assign pending ranges after freeing previous assignments
            Optional<AssignedRange> result = assignPendingRanges(sector);
            if (result.isPresent()) {
                NotificationData notification = NotificationData.newBuilder()
                        .setType(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING_CHANGED)
                        .setCounterRange(result.get())
                        .setSector(sector)
                        .setFlights(result.get().getFlights())
                        .setPendingsAhead(getPendingAssignmentsAheadOf(sector, result.get()))
                        .build();

                notificationsService.sendNotification(notification);
            }
        }

        return rangeToFree;
    }

    @Override
    public Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        readWriteLock.writeLock().lock();

        try {
            // Check if there is any contiguous range with room for another airline
            List<ContiguousRange> contiguousRangeList = ranges.get(sector);
            for (ContiguousRange range : contiguousRangeList) {
                int totalCounters = range.getCounters().size();
                if ((totalCounters - range.getOccupied()) >= count) {
                    List<Counter> countersToAdd = new ArrayList<>();
                    int counterCount = 0;

                    // Check counter list for contiguous ranges that have sufficient space
                    for (Counter counter : range.getCounters()) {
                        if (counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
                            countersToAdd.add(counter);
                            counterCount++;
                        }
                        // If there are no contiguous empty counters, empty the list and reset the counters
                        else {
                            countersToAdd.clear();
                            counterCount = 0;
                        }
                        if (counterCount == count) {
                            break;
                        }
                    }

                    // If counters quantity equals required counters then change counters status and add assigned range
                    if (countersToAdd.size() == count) {
                        countersToAdd.forEach(counter -> counter.setStatus(CounterStatus.READY_FOR_CHECKIN));
                        final AssignedRange result = finishSetupOfAssignedRange(count, airline, countersToAdd, sector, flights);
                        range.occupy(count);

                        return Optional.of(result);
                    }
                }
            }

            return Optional.empty();
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        readWriteLock.readLock().lock();
        try {
            for (AssignedRange assignedRange : assignedRanges) {
                if (assignedRange.getStart() <= rangeId && assignedRange.getEnd() >= rangeId && airline.equals(assignedRange.getAirline())) {
                    return Optional.of(assignedRange);
                }
            }
            return Optional.empty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirlineBySector(int rangeId, Airline airline, Sector sector) {
        List<AssignedRange> assignedRanges = onGoingAirlineRange.get(sector);
        return searchAssignedRangeForAirline(assignedRanges, rangeId, airline);
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters() {
        readWriteLock.readLock().lock();
        try {
            final Map<Sector, List<AssignedRange>> result = new HashMap<>();

            // Add assigned ranges to airline
            for (Sector sector : onGoingAirlineRange.keySet()) {
                result.put(sector, new ArrayList<>(onGoingAirlineRange.get(sector)));
            }

            // Add not assigned counters
            for (Sector sector : countersBySector.keySet()) {
                final List<Counter> unassignedCounters = new LinkedList<>();
                for (Counter counter : countersBySector.get(sector)) {
                    if (counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
                        unassignedCounters.add(counter);
                    }
                }

                if (!unassignedCounters.isEmpty()) {
                    final List<AssignedRange> unassignedRanges = obtainRanges(unassignedCounters);
                    result.put(sector, unassignedRanges);
                }
            }
            return result;

        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            final Map<Sector, List<AssignedRange>> result = new HashMap<>();
            result.put(sector, new ArrayList<>(onGoingAirlineRange.get(sector)));

            final List<Counter> unassignedCounters = new LinkedList<>();
            for (Counter counter : countersBySector.get(sector)) {
                if (counter.getStatus() == CounterStatus.PENDING_ASSIGNATION) {
                    unassignedCounters.add(counter);
                }
            }

            if (!unassignedCounters.isEmpty()) {
                final List<AssignedRange> unassignedRanges = obtainRanges(unassignedCounters);
                result.put(sector, unassignedRanges);
            }

            return result;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int getPendingAssignmentsAheadOf(Sector sector, AssignedRange range) {
        readWriteLock.readLock().lock();
        try {
            final Queue<AssignedRange> queue = pendingAirlineRange.get(sector);

            return IntStream.range(0, queue.size())
                    .filter(i -> queue.toArray()[i].equals(range))
                    .findFirst()
                    .orElse(-1);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean airportContainsAtLeastOneCounter() {
        readWriteLock.readLock().lock();
        try {
            return !countersBySector.isEmpty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    // Given a Counter list, it returns a list of Ranges
    // Eg. [1, 2, 3, 5, 6, 7, 8] -> [1-3, 5-8]
    private List<AssignedRange> obtainRanges(List<Counter> countersToAdd) {
        final List<AssignedRange> result = new LinkedList<>();

        for (int i = 0; i < countersToAdd.size(); i++) {

            final int start = countersToAdd.get(i).getNumber();

            while (i < countersToAdd.size() - 1 && countersToAdd.get(i).getNumber() + 1 == countersToAdd.get(i + 1).getNumber()) {
                i++;
            }
            if (i == countersToAdd.size() - 1) {
                final int end = countersToAdd.get(i).getNumber();
                final AssignedRange assignedRange = new AssignedRange(start, end, null, null, end - start + 1);
                result.add(assignedRange);

                break;
            }

            int end = countersToAdd.get(i).getNumber();

            final AssignedRange assignedRange = new AssignedRange(start, end, null, null, end - start + 1);
            result.add(assignedRange);
        }

        return result;
    }

    private AssignedRange finishSetupOfAssignedRange(int count, Airline airline, List<Counter> countersToAdd, Sector sector, List<Flight> flights) {
        final AssignedRange assignedRange = new AssignedRange(countersToAdd.get(0).getNumber(), countersToAdd.get(countersToAdd.size() - 1).getNumber(), sector, airline, count);
        for(Counter counter : countersToAdd) {
            assignedRange.addCounter(counter);
        }

        assignedRange.getFlights().addAll(flights);
        for (Flight flight : flights) {
            checkInService.addAvailableRangeForFlight(flight, assignedRange);
        }
        historyService.addAssignedRange(assignedRange);
        onGoingAirlineRange.get(sector).add(assignedRange);

        return assignedRange;
    }

    private Optional<AssignedRange> assignPendingRanges(Sector sector) {
        if (!pendingAirlineRange.get(sector).isEmpty()) {
            for (AssignedRange pendingRange : pendingAirlineRange.get(sector)) {
                Optional<AssignedRange> assigned = assignCounterRangeToAirline(pendingRange.getSector(), pendingRange.getAirline(), pendingRange.getFlights(), pendingRange.getTotalCounters());
                if (assigned.isPresent()) {
                    pendingAirlineRange.get(sector).remove(pendingRange);
                    return Optional.of(pendingRange);
                }
            }
        }

        return Optional.empty();
    }
}
