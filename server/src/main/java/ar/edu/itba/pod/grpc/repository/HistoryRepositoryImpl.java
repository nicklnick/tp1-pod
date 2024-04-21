package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.HashMap;
import java.util.Map;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Sector, CheckIn> sectorCheckInHistory = new HashMap<>();
    private final Map<Airline, CheckIn> airlineCheckInHistory = new HashMap<>();

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
        sectorCheckInHistory.put(sector, checkIn);
    }

    @Override
    public synchronized Map<Airline, CheckIn> getAirlineCheckInHistory() {
        return airlineCheckInHistory;
    }

    @Override
    public boolean containsCheckInForSector(Sector sector) {
        return sectorCheckInHistory.containsKey(sector);
    }
}
