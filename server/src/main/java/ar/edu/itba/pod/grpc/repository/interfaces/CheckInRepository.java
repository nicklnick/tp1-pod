package ar.edu.itba.pod.grpc.repository.interfaces;

public interface CheckInRepository {

    public void addCounterRangeToFlight(String flight, String sectorName, int start, int end);

}
