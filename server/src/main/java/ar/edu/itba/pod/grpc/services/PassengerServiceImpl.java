package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.PassengerStatus;
import ar.edu.itba.pod.grpc.repository.PassengerRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PassengerServiceImpl implements PassengerService {
    private static final PassengerRepository passengerRepo  = PassengerRepositoryImpl.getInstance();

    @Override
    public void addExpectedPassenger(Booking booking, Flight flight) throws IllegalArgumentException {
        if(passengerRepo.containsPassengerWithBooking(booking))
            throw new IllegalArgumentException("Passenger with given booking already exists");
        else if (passengerRepo.containsFlightWithAnotherAirline(flight))
            throw new IllegalArgumentException("Flight already exists with another airline");

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

    @Override
    public boolean existsExpectedPassengerFromAirline(Airline airline) {
        return passengerRepo.existsExpectedPassengerFromAirline(airline);
    }

    @Override
    public boolean eachFlightIsExpectingAtLeastOnePassenger(Airline airline, List<Flight> flights) {
        boolean hasExpectedPassengers = false;
        Map<Booking, Flight> expectedPassengers = passengerRepo.listExpectedPassengers();
        List<Flight> assignedFlights = new ArrayList<>(flights);

        for (Booking booking : expectedPassengers.keySet()) {
            if (assignedFlights.contains(expectedPassengers.get(booking))) {
                assignedFlights.remove(expectedPassengers.get(booking));
                hasExpectedPassengers = true;

                if (!expectedPassengers.get(booking).getAirline().equals(airline))
                    throw new IllegalArgumentException("Flight does not belong to given airline");

                if(assignedFlights.isEmpty())
                    break;
                else
                    continue;
            }

            hasExpectedPassengers = false;
        }

        return hasExpectedPassengers;
    }
}
