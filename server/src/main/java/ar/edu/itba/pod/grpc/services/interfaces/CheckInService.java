package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Sector;

public interface CheckInService {
    void counterCheckIn(Sector sector, int rangeId, Airline airline);

    AssignedRange getAvailableRangeForCheckIn(Booking booking);

    AssignedRange placePassengerInAssignedRangeQueue(Booking booking, Sector sector, int rangeId);

    AssignedRange getPassengerCheckInStatus(Booking booking);
}
