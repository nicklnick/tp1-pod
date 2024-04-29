package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HistoryRepositoryImpl implements HistoryRepository {

    private static HistoryRepositoryImpl instance;

    private final Map<Booking, CheckIn> passengerCheckInHistory = new HashMap<>();
    private final Map<Sector, List<CheckIn>> sectorCheckInHistory = new HashMap<>();
    private final Map<Airline, List<CheckIn>> airlineCheckInHistory = new HashMap<>();
    private final Map<Counter, List<CheckIn>> counterCheckInHistory = new HashMap<>();
    private final List<AssignedRange> assignedRangesHistory = new LinkedList<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    private HistoryRepositoryImpl() {
    }

    public synchronized static HistoryRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HistoryRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addCheckIn(CheckIn checkIn) {
        readWriteLock.writeLock().lock();

        try {
            sectorCheckInHistory.putIfAbsent(checkIn.getSector(), new LinkedList<>());
            sectorCheckInHistory.get(checkIn.getSector()).add(checkIn);

            airlineCheckInHistory.putIfAbsent(checkIn.getAirline(), new LinkedList<>());
            airlineCheckInHistory.get(checkIn.getAirline()).add(checkIn);

            counterCheckInHistory.putIfAbsent(checkIn.getCounter(), new LinkedList<>());
            counterCheckInHistory.get(checkIn.getCounter()).add(checkIn);

            passengerCheckInHistory.put(checkIn.getBooking(), checkIn);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Map<Airline, List<CheckIn>> getAirlineCheckInHistory() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(airlineCheckInHistory);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<Sector, List<CheckIn>> getSectorCheckInHistory() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(sectorCheckInHistory);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CheckIn> getSectorCheckInHistory(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return List.copyOf(sectorCheckInHistory.getOrDefault(sector, new LinkedList<>()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CheckIn> getAirlineCheckInHistory(Airline airline) {
        readWriteLock.readLock().lock();
        try {
            return List.copyOf(airlineCheckInHistory.getOrDefault(airline, new LinkedList<>()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<Counter, List<CheckIn>> getCounterCheckInHistory() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(counterCheckInHistory);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CheckIn> getCounterCheckInHistory(Counter counter) {
        readWriteLock.readLock().lock();
        try {
            return List.copyOf(counterCheckInHistory.getOrDefault(counter, new LinkedList<>()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsCheckInForSector(Sector sector) {
        readWriteLock.readLock().lock();
        try {
            return  sectorCheckInHistory.containsKey(sector);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean passangerDidCheckin(Booking passenger) {
        readWriteLock.readLock().lock();
        try {
            return passengerCheckInHistory.containsKey(passenger);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public CheckIn getPassengerCheckIn(Booking passenger) {
        readWriteLock.readLock().lock();
        try {
            return passengerCheckInHistory.getOrDefault(passenger, null);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        readWriteLock.readLock().lock();
        try {
            return passengerCheckInHistory.values().stream().toList();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<AssignedRange> getAssignedRangesHistory() {
        readWriteLock.readLock().lock();
        try {
            return List.copyOf(assignedRangesHistory);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void addAssignedRange(AssignedRange assignedRange) {
        readWriteLock.writeLock().lock();
        try {
            assignedRangesHistory.add(assignedRange);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
