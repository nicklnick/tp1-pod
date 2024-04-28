package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.*;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Booking, CheckIn> passengerCheckInHistory = new HashMap<>();
    private final Map<Sector, List<CheckIn>> sectorCheckInHistory = new HashMap<>();
    private final Map<Airline, List<CheckIn>> airlineCheckInHistory = new HashMap<>();
    private final Map<Counter, List<CheckIn>> counterCheckInHistory = new HashMap<>();
    private final List<AssignedRange> assignedRangesHistory = new LinkedList<>();

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
        sectorCheckInHistory.putIfAbsent(checkIn.getSector(), new LinkedList<>());
        sectorCheckInHistory.get(checkIn.getSector()).add(checkIn);

        airlineCheckInHistory.putIfAbsent(checkIn.getAirline(), new LinkedList<>());
        airlineCheckInHistory.get(checkIn.getAirline()).add(checkIn);

        counterCheckInHistory.putIfAbsent(checkIn.getCounter(), new LinkedList<>());
        counterCheckInHistory.get(checkIn.getCounter()).add(checkIn);

        passengerCheckInHistory.put(checkIn.getBooking(), checkIn);
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
        return List.copyOf(sectorCheckInHistory.getOrDefault(sector, new LinkedList<>()));
    }

    @Override
    public List<CheckIn> getAirlineCheckInHistory(Airline airline) {
        return List.copyOf(airlineCheckInHistory.getOrDefault(airline, new LinkedList<>()));
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory() {
        return Map.copyOf(counterCheckInHistory);
    }

    @Override
    public List<CheckIn> getCounterCheckInHistory(Counter counter) {
        return List.copyOf(counterCheckInHistory.getOrDefault(counter, new LinkedList<>()));
    }
    @Override
    public boolean containsCheckInForSector(Sector sector) {
        return sectorCheckInHistory.containsKey(sector);
    }

    @Override
    public boolean passangerDidCheckin(Booking passenger) {
        return passengerCheckInHistory.containsKey(passenger);
    }

    @Override
    public CheckIn getPassengerCheckIn(Booking passenger) {
        return passengerCheckInHistory.getOrDefault(passenger, null);
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        return passengerCheckInHistory.values().stream().toList();
    }

    @Override
    public List<AssignedRange> getAssignedRangesHistory() {
        return List.copyOf(assignedRangesHistory);
    }

    @Override
    public void addAssignedRange(AssignedRange assignedRange) {
        assignedRangesHistory.add(assignedRange);
    }

}
