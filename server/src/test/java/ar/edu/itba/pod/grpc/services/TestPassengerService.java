package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Booking;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.utils.Triple;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestPassengerService {

    private final PassengerService passengerService = new PassengerServiceImpl();

    @Test
    public void addExpectedPassenger() {
        List<Triple<String, String, String>> passengersData = new ArrayList<>();
        passengersData.add(new Triple<>("X100", "AA100", "AirlineA"));
        passengersData.add(new Triple<>("X101", "AA100", "AirlineA"));
        passengersData.add(new Triple<>("X102", "AA100", "AirlineA"));
        passengersData.add(new Triple<>("Y200", "BB200", "AirlineB"));
        passengersData.add(new Triple<>("Y201", "BB200", "AirlineB"));

        // Create entities
        for (Triple<String, String, String> passengerData : passengersData) {
            Booking booking = new Booking(passengerData.getFirst());
            Airline airline = new Airline(passengerData.getThird());
            Flight flight = new Flight(airline, passengerData.getSecond());

            passengerService.addExpectedPassenger(booking, flight);
        }

        Map<Booking, Flight> expectedPassengers = passengerService.listExpectedPassengers();

        Set<Booking> expectedPassengersBookings = expectedPassengers.keySet();
        Assert.assertEquals(expectedPassengersBookings.size(), passengersData.size());

        List<Flight> expectedPassengersFlights = expectedPassengers.values().stream().toList();
        Assert.assertEquals(expectedPassengersFlights.size(), passengersData.size());

        // Check if flights were placed properly with corresponding airlines
        String[] airlineAFlight100BookingCodes = {"X100", "X101", "X102"};
        Airline airlineA = new Airline("AirlineA");
        Flight airlineAFlight100 = new Flight(airlineA,"AA100");
        for (String bookingCode:
                airlineAFlight100BookingCodes) {
            Booking booking = new Booking(bookingCode);
            Flight flight = expectedPassengers.get(booking);

            Assert.assertNotNull(flight);
            Assert.assertEquals(flight, airlineAFlight100);
        }

        String[] airlineBFlight200BookingCodes = {"Y200", "Y201"};
        Airline airlineB = new Airline("AirlineB");
        Flight airlineBFlight200 = new Flight(airlineB,"BB200");
        for (String bookingCode:
                airlineBFlight200BookingCodes) {
            Booking booking = new Booking(bookingCode);
            Flight flight = expectedPassengers.get(booking);

            Assert.assertNotNull(flight);
            Assert.assertEquals(flight, airlineBFlight200);
        }

        // Existing booking
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            Booking existingBooking = new Booking("X100");
            Airline existingAirline = new Airline("AirlineC");
            Flight existingFlight = new Flight(existingAirline, "CC200");

            passengerService.addExpectedPassenger(existingBooking, existingFlight);
        });

        // Existing flight from another airline
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            Booking existingBooking = new Booking("X103");
            Airline existingAirline = new Airline("AirlineC");
            Flight existingFlight = new Flight(existingAirline, "AA100");

            passengerService.addExpectedPassenger(existingBooking, existingFlight);
        });

    }

}
