package ar.edu.itba.pod.grpc.models;

import java.util.Objects;

public class Booking implements Comparable<Booking> {

    private final Flight flight;
    private final String code;

    public Booking(Flight flight, String code) {
        this.flight = flight;
        this.code = code;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(Booking o) {
        return this.code.compareTo(o.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(flight, booking.flight) && Objects.equals(code, booking.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flight, code);
    }
}
