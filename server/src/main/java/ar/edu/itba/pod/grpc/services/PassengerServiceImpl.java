package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;
import ar.edu.itba.pod.grpc.repository.PassengerRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.Map;

public class PassengerServiceImpl implements PassengerService {
    private static final PassengerRepository passengerRepo  = PassengerRepositoryImpl.getInstance();

    @Override
    public void addExpectedPassenger(Booking booking, Flight flight) throws IllegalArgumentException {
        if(passengerRepo.containsPassengerWithBooking(booking))
            throw new IllegalArgumentException("Passenger with given booking already exists");
        else if (passengerRepo.containsPassengerWithFlight(flight))
            throw new IllegalArgumentException("Passenger with given flight already exists");

        passengerRepo.addExpectedPassenger(booking, flight);
    }

    @Override
    public Map<Booking, Flight> listExpectedPassengers() {
        return passengerRepo.listExpectedPassengers();
    }

    @Override
    public Map<Booking, PassengerStatus> listPassengerStatus() {
        return passengerRepo.listPassengerStatus();
    }

    @Override
    public void changePassengerStatus(Booking booking, PassengerStatus status) {
        if(!passengerRepo.containsPassengerWithBooking(booking))
            throw new IllegalArgumentException("Passenger with given booking does not exists");

        passengerRepo.changePassengerStatus(booking, status);
    }

    @Override
    public boolean containsPassengerWithBooking(Booking booking) {
        return passengerRepo.containsPassengerWithBooking(booking);
    }

    @Override
    public boolean passengerDidCheckIn(Booking booking) {
        return passengerRepo.listPassengerStatus().get(booking) == PassengerStatus.FINISHED_CHECKIN;
    }

    @Override
    public Booking getPassengerBooking(String bookingId) {
        Booking booking = new Booking(bookingId);
        return passengerRepo.getPassengerBooking(booking);
    }
}
