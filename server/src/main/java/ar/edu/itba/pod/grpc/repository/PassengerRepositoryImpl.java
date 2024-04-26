package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;

import java.util.HashMap;
import java.util.Map;

public class PassengerRepositoryImpl implements PassengerRepository {

    private static PassengerRepositoryImpl instance;
    private final Map<Booking, Flight> expectedPassengers = new HashMap<>();
    private final Map<Booking, PassengerStatus> passengerStatus = new HashMap<>();

    private PassengerRepositoryImpl() {
    }

    public synchronized static PassengerRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new PassengerRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addExpectedPassenger(Booking booking, Flight flight) {
        expectedPassengers.put(booking, flight);
        passengerStatus.put(booking, PassengerStatus.PENDING_CHECKIN);
    }

    @Override
    public Map<Booking, Flight> listExpectedPassengers() {
        return Map.copyOf(expectedPassengers);
    }

    @Override
    public Map<Booking, PassengerStatus> listPassengerStatus() {
        return Map.copyOf(passengerStatus);
    }

    @Override
    public boolean containsPassengerWithBooking(Booking booking) {
        return expectedPassengers.containsKey(booking);
    }

    @Override
    public boolean containsPassengerWithFlight(Flight flight) {
        return expectedPassengers.containsValue(flight);
    }

    @Override
    public void changePassengerStatus(Booking booking, PassengerStatus status) {
        passengerStatus.put(booking, status);
    }

    @Override
    public Booking getPassengerBooking(Booking booking) {
        return expectedPassengers.keySet().stream().filter(b -> b.equals(booking)).findFirst().orElse(null);
    }
}
