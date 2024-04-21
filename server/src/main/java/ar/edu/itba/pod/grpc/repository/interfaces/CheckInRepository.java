package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;

public interface CheckInRepository {

    void counterCheckIn(AssignedRange assignedRange);

    AssignedRange getAvailableRangeForCheckIn(Booking booking);
}
