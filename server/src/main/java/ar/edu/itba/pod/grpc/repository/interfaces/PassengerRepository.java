package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;

import java.util.Map;

public interface PassengerRepository {

    void addExpectedPassenger(Booking booking, Flight flight);

    Map<Booking, Flight> listExpectedPassengers();

    Map<Booking, PassengerStatus> listPassengerStatus();

    Booking getPassengerBooking(Booking booking);

    boolean containsPassengerWithBooking(Booking booking);

    boolean containsFlightWithAnotherAirline(Flight flight);

    boolean existsExpectedPassengerFromAirline(Airline airline);

    void changePassengerStatus(Booking booking, PassengerStatus status);
}
