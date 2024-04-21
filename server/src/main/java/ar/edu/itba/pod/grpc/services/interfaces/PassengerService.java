package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;

import java.util.Map;

public interface PassengerService {
    void addExpectedPassenger(Booking booking, Flight flight);

    Map<Booking, Flight> listExpectedPassengers();

    Map<Booking, PassengerStatus> listPassengerStatus();

    void changePassengerStatus(Booking booking, PassengerStatus status);

    boolean containsPassengerWithBooking(Booking booking);

    boolean passengerDidCheckIn(Booking booking);
}
