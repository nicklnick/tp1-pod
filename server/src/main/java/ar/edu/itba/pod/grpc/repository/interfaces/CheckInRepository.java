package ar.edu.itba.pod.grpc.repository.interfaces;

public interface CheckInRepository {


    void counterCheckIn(String sectorName, int rangeId, String airlineName);
}
