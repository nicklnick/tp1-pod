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
        passengerStatus.put(booking, PassengerStatus.NOT_CHECKED_IN);
    }

    @Override
    public Map<Booking, Flight> listExpectedPassengers() {
        return expectedPassengers;
    }

    @Override
    public Map<Booking, PassengerStatus> listPassengerStatus() {
        return passengerStatus;
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
}
