package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;
import java.util.Map;

public interface HistoryRepository {
    void addCheckIn(Sector sector, CheckIn checkIn);

    Map<Airline, List<CheckIn>> getAirlineCheckInHistory();

    Map<Sector, List<CheckIn>> getSectorCheckInHistory();

    Map<Sector, List<CheckIn>> getSectorCheckInHistory(Sector sector);

    Map<Airline, List<CheckIn>> getAirlineCheckInHistory(Airline airline);

    Map<Counter, List<CheckIn>> getCounterCheckInHistory();

    Map<Counter, List<CheckIn>> getCounterCheckInHistory(Counter counter);

    boolean containsCheckInForSector(Sector sector);
}
