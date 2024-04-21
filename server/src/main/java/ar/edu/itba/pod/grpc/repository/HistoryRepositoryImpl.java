package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Sector, List<CheckIn>> sectorCheckInHistory = new HashMap<>();
    private final Map<Airline, List<CheckIn>> airlineCheckInHistory = new HashMap<>();
    private final Map<Counter, List<CheckIn>> counterCheckInHistory = new HashMap<>();

    private HistoryRepositoryImpl() {
    }

    public synchronized static HistoryRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HistoryRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addCheckIn(Sector sector, CheckIn checkIn) {
        sectorCheckInHistory.putIfAbsent(sector, new ArrayList<>());
        sectorCheckInHistory.get(sector).add(checkIn);

        airlineCheckInHistory.putIfAbsent(checkIn.getAirline(), new ArrayList<>());
        airlineCheckInHistory.get(checkIn.getAirline()).add(checkIn);

        counterCheckInHistory.putIfAbsent(checkIn.getCounter(), new ArrayList<>());
        counterCheckInHistory.get(checkIn.getCounter()).add(checkIn);
    }

    @Override
    public synchronized Map<Airline, List<CheckIn>> getAirlineCheckInHistory() {
        return Map.copyOf(airlineCheckInHistory);
    }

    @Override
    public synchronized Map<Sector, List<CheckIn>> getSectorCheckInHistory() {
        return Map.copyOf(sectorCheckInHistory);
    }

    @Override
    public Map<Sector, List<CheckIn>> getSectorCheckInHistory(Sector sector) {
        final Map<Sector, List<CheckIn>> result = new HashMap<>();
        for (CheckIn checkIn : sectorCheckInHistory.get(sector)) {
            result.putIfAbsent(checkIn.getSector(), new ArrayList<>());
            result.get(checkIn.getSector()).add(checkIn);
        }

        return result;
    }

    @Override
    public Map<Airline, List<CheckIn>> getAirlineCheckInHistory(Airline airline) {
        final Map<Airline, List<CheckIn>> result = new HashMap<>();
        for (CheckIn checkIn : airlineCheckInHistory.get(airline)) {
            result.putIfAbsent(checkIn.getAirline(), new ArrayList<>());
            result.get(checkIn.getAirline()).add(checkIn);
        }

        return result;
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory() {
        return Map.copyOf(counterCheckInHistory);
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory(Counter counter) {
        final Map<Counter, List<CheckIn>> result = new HashMap<>();
        for (CheckIn checkIn : counterCheckInHistory.get(counter)) {
            result.putIfAbsent(checkIn.getCounter(), new ArrayList<>());
            result.get(checkIn.getCounter()).add(checkIn);
        }

        return result;
    }

    @Override
    public boolean containsCheckInForSector(Sector sector) {
        return sectorCheckInHistory.containsKey(sector);
    }
}
