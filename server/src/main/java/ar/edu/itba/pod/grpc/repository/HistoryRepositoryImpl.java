package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Booking, CheckIn> passangerCheckInHistory = new HashMap<>();
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
    public synchronized void addCheckIn(CheckIn checkIn) {
        sectorCheckInHistory.putIfAbsent(checkIn.getSector(), new ArrayList<>());
        sectorCheckInHistory.get(checkIn.getSector()).add(checkIn);

        airlineCheckInHistory.putIfAbsent(checkIn.getAirline(), new ArrayList<>());
        airlineCheckInHistory.get(checkIn.getAirline()).add(checkIn);

        counterCheckInHistory.putIfAbsent(checkIn.getCounter(), new ArrayList<>());
        counterCheckInHistory.get(checkIn.getCounter()).add(checkIn);

        passangerCheckInHistory.put(checkIn.getBooking(), checkIn);
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
    public List<CheckIn> getSectorCheckInHistory(Sector sector) {
        return new ArrayList<>(sectorCheckInHistory.getOrDefault(sector, new ArrayList<>()));
    }

    @Override
    public List<CheckIn> getAirlineCheckInHistory(Airline airline) {
        return new ArrayList<>(airlineCheckInHistory.getOrDefault(airline, new ArrayList<>()));
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory() {
        return Map.copyOf(counterCheckInHistory);
    }

    @Override
    public List<CheckIn> getCounterCheckInHistory(Counter counter) {
        return new ArrayList<>(counterCheckInHistory.getOrDefault(counter, new ArrayList<>()));
    }
    @Override
    public boolean containsCheckInForSector(Sector sector) {
        return sectorCheckInHistory.containsKey(sector);
    }

    @Override
    public boolean passangerDidCheckin(Booking passenger) {
        return passangerCheckInHistory.containsKey(passenger);
    }

    @Override
    public CheckIn getPassengerCheckIn(Booking passenger) {
        return passangerCheckInHistory.getOrDefault(passenger, null);
    }
}
