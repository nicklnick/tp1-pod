package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.CheckInRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;
import ar.edu.itba.pod.grpc.services.interfaces.CheckInService;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CheckInServiceImpl implements CheckInService {
    private static final CheckInRepository checkInRepository = CheckInRepositoryImpl.getInstance();
    private  final PassengerService passengerService = new PassengerServiceImpl();
    private final SectorService sectorService = new SectorServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();

    public static CheckInRepository getCheckInRepository() {
        return checkInRepository;
    }

    @Override
    public void counterCheckIn(Sector sector, int rangeId, Airline airline) {
        if(!sectorService.containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        final List<AssignedRange> onGoingAirlineRange = sectorService.getOnGoingAirlineRange().get(sector);
        final Optional<AssignedRange> maybeAssignedRange = sectorService.searchAssignedRangeForAirline(onGoingAirlineRange, rangeId, airline);
        if(maybeAssignedRange.isEmpty())
            throw new IllegalArgumentException("Airline does not have that range assigned");

        final AssignedRange assignedRange = maybeAssignedRange.get();
        if(assignedRange.getQueueSize() == 0)
            return;

        checkInRepository.counterCheckIn(assignedRange);
    }

    @Override
    public AssignedRange getAvailableRangeForCheckIn(Booking booking) {
        if(!passengerService.containsPassengerWithBooking(booking)) {
            throw new IllegalArgumentException("Not expecting any passengers with given booking");
        }
        return checkInRepository.getAvailableRangeForCheckIn(booking);
    }

    // tiene que devolver todos los datos del rango
    @Override
    public AssignedRange placePassengerInAssignedRangeQueue(Booking booking, Sector sector, int rangeId) {
        if(!passengerService.containsPassengerWithBooking(booking)) {
            throw new IllegalArgumentException("Not expecting any passengers with given booking");
        } else if (!sectorService.containsSector(sector)) {
            throw new IllegalArgumentException("Sector does not exist");
        }
        Flight flight = passengerService.listExpectedPassengers().get(booking);
        Optional<AssignedRange> maybeAssignedRange = sectorService.searchAssignedRangeForAirline(
                sectorService.getOnGoingAirlineRange().get(sector), rangeId, flight.getAirline());
        if(maybeAssignedRange.isEmpty()) {
            throw new IllegalArgumentException("Counter number does not correspond to the beginning of a range of counters " +
                    "assigned to the airline accepting passengers on the given booking flight");
        }
        AssignedRange assignedRange = maybeAssignedRange.get();
        if(assignedRange.getPassengers().contains(booking)) {
            throw new IllegalArgumentException("Passenger already in counter range queue");
        } else if (passengerService.passengerDidCheckIn(booking)) {
            throw new IllegalArgumentException("Passenger already checked-in for given booking");
        }
        //entra a la fila
        assignedRange.getPassengers().add(booking);
        passengerService.changePassengerStatus(booking, PassengerStatus.ONGOING_CHECKIN);
        return assignedRange;
    }


    @Override
    public AssignedRange getPassengerCheckInStatus(Booking booking) {
        if(!passengerService.containsPassengerWithBooking(booking)) {
            throw new IllegalArgumentException("Not expecting any passengers with given booking");
        }
        Flight flight = passengerService.listExpectedPassengers().get(booking);
        Optional<Airline> maybeAirline = Optional.of(flight.getAirline());
        if(passengerService.passengerDidCheckIn(booking)) {
            //deberia devolver esto, pero deberia llamarse desde el servant
            return null;
        }

        Map<Sector, List<AssignedRange>> onGoingAirlineRange = sectorService.getOnGoingAirlineRange();
        for (Map.Entry<Sector, List<AssignedRange>> entry : onGoingAirlineRange.entrySet()) {
            for (AssignedRange assignedRange : entry.getValue()) {
                if(assignedRange.getPassengers().contains(booking)) {
                    //deberia devolver esto
                    return assignedRange;
                }
            }
        }
        return getAvailableRangeForCheckIn(booking);
    }

    @Override
    public void addAvailableRangeForFlight(Flight flight, AssignedRange assignedRange) {
        checkInRepository.addAvailableRangeForFlight(flight, assignedRange);
    }
}
