package ar.edu.itba.pod.grpc.models;

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
}
