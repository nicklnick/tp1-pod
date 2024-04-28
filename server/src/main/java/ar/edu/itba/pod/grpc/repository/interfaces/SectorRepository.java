package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public interface SectorRepository {
    void addSector(Sector sector);

    Map<Sector, List<Counter>> listSectors();

    boolean containsSector(Sector sector);

    ContiguousRange addCountersToSector(Sector sector, int count);

    Map<Sector, List<ContiguousRange>> getContiguousRanges();

    List<ContiguousRange> getContiguosRangesBySector(Sector sector);

    Map<Sector, List<AssignedRange>> getOnGoingAirlineRange();

    List<AssignedRange> getOnGoingAirlineRangeBySector(Sector sector);

    Queue<AssignedRange> getPendingAirlineRange(Sector sector);

    Optional<AssignedRange> freeAssignedRange(Sector sector, Airline airline, int rangeId);

    Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count);

    Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline);

    Optional<AssignedRange> searchAssignedRangeForAirlineBySector(int rangeId, Airline airline, Sector sector);

    Map<Sector, List<AssignedRange>> listCounters();

    Map<Sector, List<AssignedRange>> listCounters(Sector sector);

    int getPendingAssignmentsAheadOf(Sector sector, AssignedRange range);

    boolean airportContainsAtLeastOneCounter();
}
