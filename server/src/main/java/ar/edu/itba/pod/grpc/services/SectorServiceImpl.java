package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.SectorRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SectorServiceImpl implements SectorService {
    private static final SectorRepository sectorRepo = SectorRepositoryImpl.getInstance();
    private final NotificationsService notificationsService = new NotificationsServiceImpl();
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public void addSector(String name) {
        final Sector sector = new Sector(name);
        if (containsSector(sector))
            throw new IllegalArgumentException("Sector already exists");

        sectorRepo.addSector(sector);
    }

    @Override
    public Map<Sector, List<Counter>> listSectors() {
        final Map<Sector, List<Counter>> result = sectorRepo.listSectors();
        if(result.isEmpty())
            throw new IllegalStateException("No sectors found");

        return result;
    }

    @Override
    public ContiguousRange addCountersToSector(Sector sector, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

        if (!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        return sectorRepo.addCountersToSector(sector, count);
    }

    @Override
    public Map<Sector, List<ContiguousRange>> getContiguousRanges() {
        final Map<Sector, List<ContiguousRange>> result = sectorRepo.getContiguousRanges();
        if(result.isEmpty())
            throw new IllegalStateException("No sectors found");

        return result;
    }

    @Override
    public Map<Sector, List<AssignedRange>> getOnGoingAirlineRange() {
        return sectorRepo.getOnGoingAirlineRange();
    }

    @Override
    public Queue<AssignedRange> getPendingAirlineRange(Sector sector) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        return sectorRepo.getPendingAirlineRange(sector);
    }

    @Override
    public Optional<AssignedRange> freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        if(rangeId <= 0)
            throw new IllegalArgumentException("RangeId must be greater than 0");

        if (!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        return sectorRepo.freeAssignedRange(sector, airline, rangeId);
    }

    @Override
    public Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        if(count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        Optional<AssignedRange> result;
        boolean isPending = false;
        readWriteLock.writeLock().lock();
        try {
            boolean hasExpectedPassengers = passengerService.eachFlightIsExpectingAtLeastOnePassenger(airline, flights);
            if (!hasExpectedPassengers)
                throw new IllegalArgumentException("Not expecting any passengers for at least one of the given flights");

            // Check if airline has already been assigned to a range or is pending assignment
            for (AssignedRange assignedRange : sectorRepo.getOnGoingAirlineRange().get(sector)) {
                if (assignedRange.getAirline().equals(airline)) {
                    for (Flight flight : flights) {
                        if (assignedRange.getFlights().contains(flight)) {
                            throw new IllegalArgumentException("Range already assigned for at least one of the given flights");
                        }
                    }
                }
            }

            // Check if airline has already a pending range assignment
            for (AssignedRange assignedRange : sectorRepo.getPendingAirlineRange(sector)) {
                if (assignedRange.getAirline().equals(airline)) {
                    for (Flight flight : flights) {
                        if (assignedRange.getFlights().contains(flight)) {
                            throw new IllegalArgumentException("Pending range assignment already existing for at least one of the given flights");
                        }
                    }
                }
            }

            // Check if airline has already started checkin process
            if (historyService.airlineHasStartedCheckInOnFlights(airline, flights))
                throw new IllegalArgumentException("Flight check-in can't start more than once");

            result = sectorRepo.assignCounterRangeToAirline(sector, airline, flights, count);
            if(result.isEmpty()) {
                isPending = true;
                result = Optional.of(new AssignedRange(sector, airline, count));
                sectorRepo.getPendingAirlineRange(sector).add(result.get());
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }

        if(notificationsService.isRegisteredForNotifications(airline)) {
            NotificationData notification = NotificationData.newBuilder()
                    .setAirline(airline)
                    .setSector(sector)
                    .setFlights(flights)
                    .setCounterRange(result.get())
                    .setType(isPending? NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING : NotificationType.NOTIFICATION_ASSIGNED_COUNTERS)
                    .build();

            notificationsService.sendNotification(notification);
        }

        return result;
    }

    @Override
    public List<Range> getRangesBySector(Sector sector, int from, int to) {
        if( to < from)
            throw new IllegalArgumentException("To cannot be lower than from");

        List<AssignedRange> assignedRangeList;
        List<ContiguousRange> contiguousRangeList;
        readWriteLock.readLock().lock();
        try {
             assignedRangeList = sectorRepo.getOnGoingAirlineRangeBySector(sector);
             contiguousRangeList = sectorRepo.getContiguosRangesBySector(sector);
        } finally {
            readWriteLock.readLock().unlock();
        }

        List<Range> ranges = new ArrayList<>();
        contiguousRangeList.sort(Comparator.comparingInt(Range::getStart));
        assignedRangeList.sort(Comparator.comparingInt(Range::getStart));

        for (ContiguousRange contiguous:contiguousRangeList) {
            int start = contiguous.getStart();
            int end = contiguous.getEnd();

            if((start >= from || end <= to) && start <= to && end >= from) {
                int lastEndSeen = contiguous.getStart();
                for (AssignedRange current:assignedRangeList) {
                    if(current.getEnd() < contiguous.getStart())
                        continue;
                    if(current.getStart() >= contiguous.getEnd())
                        break;

                    if(current.getStart()-1 > lastEndSeen){
                        int inc = lastEndSeen == contiguous.getStart() ? 0 : 1;
                        ranges.add(new ContiguousRange(lastEndSeen+inc,current.getStart()-1,sector));
                    }
                    lastEndSeen = current.getEnd();
                    ranges.add(current);
                }

                if (lastEndSeen == contiguous.getStart())
                    ranges.add(new ContiguousRange(contiguous.getStart(), contiguous.getEnd(),sector));

                else if (lastEndSeen + 1 < contiguous.getEnd())
                    ranges.add(new ContiguousRange(lastEndSeen+1,contiguous.getEnd(),sector));
            }
        }

        if(ranges.isEmpty())
            throw new IllegalArgumentException("The provided range does not contain any counter");

        return ranges;
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        return sectorRepo.searchAssignedRangeForAirline(assignedRanges, rangeId, airline);
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirlineBySector(int rangeId, Airline airline, Sector sector){
        return sectorRepo.searchAssignedRangeForAirlineBySector(rangeId,airline,sector);
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters(Optional<Sector> sector) {
        if(!sectorRepo.airportContainsAtLeastOneCounter())
            throw new IllegalStateException("No counters found");

        if (sector.isEmpty())
            return sectorRepo.listCounters();

        return sectorRepo.listCounters(sector.get());
    }

    @Override
    public boolean containsSector(Sector sector) {
        return sectorRepo.containsSector(sector);
    }

    @Override
    public int getPendingAssignmentsAheadOf(Sector sector, AssignedRange range) {
        return sectorRepo.getPendingAssignmentsAheadOf(sector, range);
    }
}
