package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;

import java.util.Map;

public interface PassengerRepository {

    void addExpectedPassenger(Booking booking, Flight flight);

    Map<Booking, Flight> listExpectedPassengers();

    Map<Booking, PassengerStatus> listPassengerStatus();

    boolean containsPassengerWithBooking(Booking booking);

    boolean containsPassengerWithFlight(Flight flight);

    void changePassengerStatus(Booking booking, PassengerStatus status);
}