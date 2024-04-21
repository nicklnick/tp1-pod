package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public interface SectorService {
    void addSector(String name);

    Map<Sector, List<Counter>> listSectors();

    ContiguousRange addCountersToSector(Sector sector, int count);

    Map<Sector, List<ContiguousRange>> getContiguousRanges();

    Map<Sector, List<AssignedRange>> getOnGoingAirlineRange();

    Queue<AssignedRange> getPendingAirlineRange(Sector sector);

    void freeAssignedRange(Sector sector, Airline airline, int rangeId);

    void assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flight, int count);

    Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline);

    Map<Sector, List<AssignedRange>> listCounters(Optional<Sector> sector);

    boolean containsSector(Sector sector);
}
