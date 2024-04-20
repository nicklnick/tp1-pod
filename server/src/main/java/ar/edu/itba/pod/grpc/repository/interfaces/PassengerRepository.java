package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;

import java.util.Map;

public interface PassengerRepository {

    public void addExpectedPassenger(String booking, String flight, String airline);

    Map<Booking, Flight> getExpectedPassengers();

    Map<Booking, PassengerStatus> getPassengerStatus();
}
