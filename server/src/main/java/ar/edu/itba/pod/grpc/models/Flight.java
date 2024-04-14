package ar.edu.itba.pod.grpc.models;

import java.util.Objects;

public class Flight {

    private final Airline airline;
    private final String code;

    public Flight(Airline airline, String code) {
        this.airline = airline;
        this.code = code;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(airline, flight.airline) && Objects.equals(code, flight.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airline, code);
    }
}
