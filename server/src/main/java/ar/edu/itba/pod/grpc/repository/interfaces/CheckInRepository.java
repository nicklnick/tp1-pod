package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Flight;

import java.util.Optional;

public interface CheckInRepository {

    Optional<CheckIn> counterCheckIn(AssignedRange assignedRange);

    AssignedRange getAvailableRangeForCheckIn(Flight flight);

    void addAvailableRangeForFlight(Flight flight, AssignedRange assignedRange);
}
