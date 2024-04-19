package ar.edu.itba.pod.grpc.repository.interfaces;

public interface AirportRepository {
    public void addSector(String name);

    public void addCounterToSector(String sectorName, int number);

    public void addRangeToSector(String sectorName, int start, int end);

    public void assignCounterRangeToAirline(String sectorName, String airline,int start, int end);


}
