package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Sector;

public interface CheckInService {
    void counterCheckIn(Sector sector, int rangeId, Airline airline);
}
