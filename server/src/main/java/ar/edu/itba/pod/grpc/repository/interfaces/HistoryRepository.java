package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.HistoryRepositoryImpl;

import java.util.List;
import java.util.Map;

public interface HistoryRepository {
    void addCheckIn(CheckIn checkIn);

    Map<Airline, List<CheckIn>> getAirlineCheckInHistory();

    Map<Sector, List<CheckIn>> getSectorCheckInHistory();

    Map<Counter, List<CheckIn>> getCounterCheckInHistory();

    List<CheckIn> getSectorCheckInHistory(Sector sector);

    List<CheckIn> getAirlineCheckInHistory(Airline airline);

    List<CheckIn> getCounterCheckInHistory(Counter counter);

    boolean containsCheckInForSector(Sector sector);

    boolean passangerDidCheckin(Booking passenger);

    CheckIn getPassengerCheckIn(Booking passenger);

    List<CheckIn> getAllCheckIns();

    List<AssignedRange> getAssignedRangesHistory();

    void addAssignedRange(AssignedRange assignedRange);
}
