package ar.edu.itba.pod.grpc.models;

import java.util.Objects;

public class Passenger implements Comparable<Passenger> {

    private final Booking booking;
    private PassengerStatus status;

    public Passenger(Booking booking, PassengerStatus status) {
        this.booking = booking;
        this.status = status;
    }

    public Passenger(Booking booking) {
        this.booking = booking;
        this.status = PassengerStatus.PENDING;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setStatus(PassengerStatus status) {
        this.status = status;
    }

    public PassengerStatus getStatus() {
        return status;
    }

    @Override
    public int compareTo(Passenger o) {
        return this.booking.compareTo(o.booking);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(booking, passenger.booking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(booking);
    }
}
