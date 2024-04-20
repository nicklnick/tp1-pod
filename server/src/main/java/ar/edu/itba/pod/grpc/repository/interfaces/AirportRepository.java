package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public interface AirportRepository {
    void addSector(String name);
    void addCountersToSector(String sectorName, int count);
    Map<Sector, List<Counter>> getSectors();
    Map<Sector, List<ContiguousRange>> getContiguousRanges();
    Map<Sector, List<AssignedRange>> getSectorsCounterRange();
    void assignCounterRangeToAirline(String sectorName, List<String> flightNames, String airline, int count);
    void freeAssignedRange(String sectorName, int rangeId, String airlineName);
    boolean containsSector(Sector sector);
    Queue<AssignedRange> getpendingAirlineRange(String sectorName);
    Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline);
}
