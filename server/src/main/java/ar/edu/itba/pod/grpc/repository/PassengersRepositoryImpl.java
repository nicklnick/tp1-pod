package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;

import java.util.Map;

public class PassengersRepositoryImpl implements PassengerRepository {

    private static PassengersRepositoryImpl instance;
    private Map<Booking, Flight> expectedPassengers;
    private Map<Booking, PassengerStatus> passengerStatus;

    private PassengersRepositoryImpl() {
        throw new AssertionError("No se puede instanciar esta clase");
    }

    public synchronized static PassengersRepositoryImpl getInstance() {
        if(instance == null) {
            instance = new PassengersRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addExpectedPassenger(String booking, String flight, String airline) {
        Booking newBooking = new Booking(booking);
        Airline newAirline = new Airline(airline);
        Flight newFlight = new Flight(newAirline, flight);
        if(expectedPassengers.containsKey(newBooking)) {
            throw new IllegalArgumentException("Ya existe un pasajero con el mismo booking");
        }
        if(expectedPassengers.containsValue(newFlight)) {
            throw new IllegalArgumentException("Ya existe un pasajero con el mismo vuelo");
        }

        expectedPassengers.put(newBooking, newFlight);
        passengerStatus.put(newBooking, PassengerStatus.NOT_CHECKED_IN);
    }
    @Override
    public Map<Booking, Flight> getExpectedPassengers() {
        return expectedPassengers;
    }
    @Override
    public Map<Booking, PassengerStatus> getPassengerStatus() {
        return passengerStatus;
    }

}
