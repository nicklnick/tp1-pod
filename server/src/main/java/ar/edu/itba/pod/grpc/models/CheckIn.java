package ar.edu.itba.pod.grpc.models;

public class CheckIn {
    private final Sector sector;
    private final Counter counter;
    private final Flight flight;
    private final Airline airline;
    private final Booking booking;
    private final Range range;

    public CheckIn(Sector sector, Counter counter, Airline airline, Flight flight, Booking booking, Range range) {
        this.sector = sector;
        this.counter = counter;
        this.airline = airline;
        this.booking = booking;
        this.flight = flight;
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckIn checkIn = (CheckIn) o;
        return booking.equals(checkIn.booking);
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

    public Booking getBooking() {
        return booking;
    }

    public Flight getFlight() {
        return flight;
    }

    public Range getRange() {
        return range;
    }
}
