package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.Status;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;

import java.util.Map;

public class PassengersRepositoryImpl implements PassengerRepository {
    private Map<Booking, Flight> expectedPassengers;


    private Map<String, Status> passengers;

    @Override
    public void addExpectedPassenger(String booking, String flight, String airline) {

    }
}
