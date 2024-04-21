package ar.edu.itba.pod.grpc.models;

public class CheckIn {
    private final Sector sector;
    private final Counter counter;
    private final Flight flight;
    private final Airline airline;
    private final Booking booking;

    public CheckIn(Sector sector, Counter counter, Airline airline, Flight flight, Booking booking) {
        this.sector = sector;
        this.counter = counter;
        this.airline = airline;
        this.booking = booking;
        this.flight = flight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckIn checkIn = (CheckIn) o;
        return sector.equals(checkIn.sector) && counter.equals(checkIn.counter) && airline.equals(checkIn.airline) && booking.equals(checkIn.booking);
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

}
