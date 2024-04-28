package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.*;

import java.util.Optional;

public interface CheckInService {
    Optional<CheckIn> counterCheckIn(Sector sector, int rangeId, Airline airline);

    AssignedRange getAvailableRangeForCheckIn(Booking booking);

    AssignedRange placePassengerInAssignedRangeQueue(Booking booking, Sector sector, int rangeId);

    AssignedRange getPassengerCheckInStatus(Booking booking);

    void addAvailableRangeForFlight(Flight flight, AssignedRange assignedRange);
}
