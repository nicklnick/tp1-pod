package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.HistoryRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistoryServiceImpl implements HistoryService {
    private static final HistoryRepository historyRepo = HistoryRepositoryImpl.getInstance();


    @Override
    public void addCheckIn(Sector sector, CheckIn checkIn) {
        if (historyRepo.containsCheckInForSector(sector))
            throw new IllegalArgumentException("Ya existe un check-in para el sector indicado");

        historyRepo.addCheckIn(sector, checkIn);
    }

    @Override
    public Map<Sector, List<CheckIn>> getSectorCheckInHistory(Optional<Sector> maybeSector) {
        if (maybeSector.isEmpty())
            return historyRepo.getSectorCheckInHistory();

        return historyRepo.getSectorCheckInHistory(maybeSector.get());
    }

    @Override
    public Map<Airline, List<CheckIn>> getAirlineCheckInHistory(Optional<Airline> maybeAirline) {
        if (maybeAirline.isEmpty())
            return historyRepo.getAirlineCheckInHistory();

        return historyRepo.getAirlineCheckInHistory(maybeAirline.get());
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory(Optional<Counter> maybeCounter) {
        if (maybeCounter.isEmpty())
            return historyRepo.getCounterCheckInHistory();

        return historyRepo.getCounterCheckInHistory(maybeCounter.get());
    }
}
