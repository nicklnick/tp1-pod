package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.Map;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Sector, CheckIn> sectorCheckInHistory;
    private final Map<Airline,CheckIn> airlineCheckInHistory;

    private HistoryRepositoryImpl() {
        throw new AssertionError("No se puede instanciar esta clase");
    }

    public synchronized static HistoryRepositoryImpl getInstance() {
        if(instance == null) {
            instance = new HistoryRepositoryImpl();
        }
        return instance;
    }

    public synchronized void addCheckIn(Sector sector, CheckIn checkIn) {
        if(sectorCheckInHistory.containsKey(sector)) {
            throw new IllegalArgumentException("Ya existe un check-in para el sector indicado");
        }
        sectorCheckInHistory.put(sector, checkIn);
    }

    public synchronized Map<Airline, CheckIn> getAirlineCheckInHistory() {
        return airlineCheckInHistory;
    }


}
