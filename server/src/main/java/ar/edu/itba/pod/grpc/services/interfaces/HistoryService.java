package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HistoryService {
    void addCheckIn(Sector sector, CheckIn checkIn);

    Map<Sector, List<CheckIn>> getSectorCheckInHistory(Optional<Sector> maybeSector);

    Map<Airline, List<CheckIn>> getAirlineCheckInHistory(Optional<Airline> maybeAirline);

    Map<Counter, List<CheckIn>> getCounterCheckInHistory(Optional<Counter> maybeCounter);
}
