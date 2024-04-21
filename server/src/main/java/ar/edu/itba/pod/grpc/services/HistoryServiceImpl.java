package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.HistoryRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;

import java.util.Map;

public class HistoryServiceImpl implements HistoryService {
    private static final HistoryRepository historyRepo = HistoryRepositoryImpl.getInstance();


    @Override
    public void addCheckIn(Sector sector, CheckIn checkIn) {
        if (historyRepo.containsCheckInForSector(sector))
            throw new IllegalArgumentException("Ya existe un check-in para el sector indicado");

        historyRepo.addCheckIn(sector, checkIn);
    }

    @Override
    public Map<Airline, CheckIn> getAirlineCheckInHistory() {
        return historyRepo.getAirlineCheckInHistory();
    }
}
