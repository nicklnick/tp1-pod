package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.SectorRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class SectorServiceImpl implements SectorService {
    private static final SectorRepository sectorRepo = SectorRepositoryImpl.getInstance();
    private final NotificationsService notificationsService = new NotificationsServiceImpl();
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();

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
        if (!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if (count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

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
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(rangeId <= 0)
            throw new IllegalArgumentException("RangeId must be greater than 0");

        return sectorRepo.freeAssignedRange(sector, airline, rangeId);
    }

    @Override
    public Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        // ---- casos de error ----

        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");
        boolean hasExpectedPassengers = true;
        Map<Booking, Flight> expectedPassengers = passengerService.listExpectedPassengers();

        // chequeo si los vuelos indicados tienen pasajeros esperados
        for (Booking booking : expectedPassengers.keySet()) {
            if (flights.contains(expectedPassengers.get(booking))) {
                flights.remove(expectedPassengers.get(booking));
                hasExpectedPassengers = true;
                if (!expectedPassengers.get(booking).getAirline().equals(airline)) {
                    throw new IllegalArgumentException("Flight does not belong to given airline");
                }
                if(flights.isEmpty())
                    break;
                else
                    continue;
            }
            hasExpectedPassengers = false;
        }


        // si no hay pasajeros esperados para al menos uno de los vuelos indicados, se lanza una excepción
        if (!hasExpectedPassengers) {
            throw new IllegalArgumentException("Not expecting any passengers for any of the given flights");
        }

        // chequeo si la aerolinea ya tiene un rango asignado o pendiente
        for (AssignedRange assignedRange : sectorRepo.getOnGoingAirlineRange().get(sector)) {
            if (assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Range already assigned for at least one of the given flights");
                    }
                }
            }
        }

        // chequeo si la aerolinea ya tiene un rango pendiente
        for (AssignedRange assignedRange : sectorRepo.getPendingAirlineRange(sector)) {
            if (assignedRange.getAirline().equals(airline)) {
                for (Flight flight : flights) {
                    if (assignedRange.getFlights().contains(flight)) {
                        throw new IllegalArgumentException("Pending range assignment already existing for at least one of the given flights");
                    }
                }
            }
        }
        // chequeo si la aerolinea ya tiene un check-in iniciado
        // TODO: revisarlo
        List<CheckIn> airlineCheckIns = historyService.getAirlineCheckInHistory(Optional.of(airline));
        for (CheckIn checkIn : airlineCheckIns) {
            for (Flight flight : flights) {
                if (checkIn.getFlight().equals(flight)) {
                    throw new IllegalArgumentException("Flight check-in can't start more than once");
                }
            }
        }
        // ---- fin de casos de error ----

        Optional<AssignedRange> result = sectorRepo.assignCounterRangeToAirline(sector, airline, new ArrayList<>(flights), count);

        if (result.isPresent()) {
            NotificationData notification = NotificationData.newBuilder()
                    .setType(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS)
                    .setAirline(airline)
                    .setSector(sector)
                    .setCounterRange(result.get())
                    .setFlights(flights)
                    .build();

            notificationsService.sendNotification(notification);
        } else {
            AssignedRange pendingRange = new AssignedRange(sector, airline, count);
            sectorRepo.getPendingAirlineRange(sector).add(pendingRange);

            NotificationData notification = NotificationData.newBuilder()
                    .setType(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING)
                    .setAirline(airline)
                    .setCounterRange(pendingRange)
                    .setSector(sector)
                    .setFlights(flights)
                    .build();

            notificationsService.sendNotification(notification);
        }

        return result;
    }

    @Override
    public List<Range> getRangesBySector(Sector sector, int from, int to) {
        if( to < from)
            throw new IllegalArgumentException("To cannot be lower than from");

        List<AssignedRange> assignedRangeList = sectorRepo.getOnGoingAirlineRangeBySector(sector);
        List<ContiguousRange> contiguousRangeList = sectorRepo.getContiguosRangesBySector(sector);

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
