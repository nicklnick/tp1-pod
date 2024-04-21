package ar.edu.itba.pod.grpc.models;

import java.util.Objects;

public class Booking implements Comparable<Booking> {

    private final String code;

    public Booking(String code) {
        this.code = code;
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
        return Objects.equals(code, booking.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public String getCode() {
        return code;
    }
}
