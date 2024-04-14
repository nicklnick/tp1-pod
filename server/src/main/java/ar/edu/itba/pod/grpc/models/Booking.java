package ar.edu.itba.pod.grpc.models;

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
}
