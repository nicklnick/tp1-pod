package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PassengerRepositoryImpl implements PassengerRepository {

    private static PassengerRepositoryImpl instance;
    private final Map<Booking, Flight> expectedPassengers = new HashMap<>();
    private final Map<Booking, PassengerStatus> passengerStatus = new HashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private PassengerRepositoryImpl() {
    }

    public synchronized static PassengerRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new PassengerRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addExpectedPassenger(Booking booking, Flight flight) {
        readWriteLock.writeLock().lock();
        try {
            expectedPassengers.put(booking, flight);
            passengerStatus.put(booking, PassengerStatus.PENDING_CHECKIN);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Map<Booking, Flight> listExpectedPassengers() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(expectedPassengers);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<Booking, PassengerStatus> listPassengerStatus() {
        readWriteLock.readLock().lock();
        try {
            return Map.copyOf(passengerStatus);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsPassengerWithBooking(Booking booking) {
        readWriteLock.readLock().lock();
        try {
            return expectedPassengers.containsKey(booking);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsFlightWithAnotherAirline(Flight flight) {
        readWriteLock.readLock().lock();
        try {
            List<Flight> expectedPassengersFlights = expectedPassengers.values().stream().toList();
            Airline airline = flight.getAirline();

            for (Flight expectedPassengersFlight : expectedPassengersFlights) {
                if (expectedPassengersFlight.getCode().equals(flight.getCode()) && !expectedPassengersFlight.getAirline().equals(airline))
                    return true;
            }

            return false;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean existsExpectedPassengerFromAirline(Airline airline) {
        readWriteLock.readLock().lock();
        try {
            return expectedPassengers.values().stream().anyMatch((flight) -> flight.getAirline().equals(airline));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void changePassengerStatus(Booking booking, PassengerStatus status) {
        readWriteLock.writeLock().lock();
        try {
            passengerStatus.put(booking, status);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Booking getPassengerBooking(Booking booking) {
        readWriteLock.readLock().lock();
        try {
            return expectedPassengers.keySet().stream().filter(b -> b.equals(booking)).findFirst().orElse(null);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
