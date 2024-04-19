package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.Status;

import java.util.Map;

public class PassengersRepository {
    private Map<Booking, Flight> expectedPassengers;

    private Map<Booking, Status> passengers;
}
