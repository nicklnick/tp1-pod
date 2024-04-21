package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;
import java.util.Map;

public interface HistoryRepository {
    void addCheckIn(Sector sector, CheckIn checkIn);

    Map<Airline, List<CheckIn>> getAirlineCheckInHistory();
    Map<Sector, List<CheckIn>> getSectorCheckInHistory();

    boolean containsCheckInForSector(Sector sector);
}
