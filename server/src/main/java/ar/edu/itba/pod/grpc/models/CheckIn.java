package ar.edu.itba.pod.grpc.models;

public class CheckIn {

    private Sector sector;
    private Counter counter;
    private Airline airline;
    private Flight flight;
    private Booking booking;

    public CheckIn(Sector sector, Counter counter, Airline airline, Flight flight, Booking booking) {
        this.sector = sector;
        this.counter = counter;
        this.airline = airline;
        this.flight = flight;
        this.booking = booking;
    }

    public Sector getSector() {
        return sector;
    }

    public Counter getCounter() {
        return counter;
    }

    public Airline getAirline() {
        return airline;
    }

    public Flight getFlight() {
        return flight;
    }

    public Booking getBooking() {
        return booking;
    }
}
