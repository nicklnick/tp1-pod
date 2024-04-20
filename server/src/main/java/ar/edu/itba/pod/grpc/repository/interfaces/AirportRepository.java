package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.ContiguousRange;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;
import java.util.Map;

public interface AirportRepository {
    public void addSector(String name);

    public void addCountersToSector(String sectorName, int count);

    public Map<Sector, List<Counter>> getSectors();

    public Map<Sector, List<ContiguousRange>> getContiguousRanges();

    public Map<Sector, List<AssignedRange>> getSectorsCounterRange();


    public void assignCounterRangeToAirline(String sectorName, List<String> flightNames, String airline, int count);


}
