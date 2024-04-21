package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.AssignedRange;

public interface CheckInRepository {

    void counterCheckIn(AssignedRange assignedRange);
}
