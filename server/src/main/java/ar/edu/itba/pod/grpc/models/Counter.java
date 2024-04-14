package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class Counter implements Comparable<Counter> {

    private final int number;
    private final Sector sector;
    private Airline airline;
    private List<Flight> flights;

    public Counter(Sector sector, int number) {
        this.sector = sector;
        this.number = number;
        this.flights = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public Sector getSector() {
        return sector;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public int compareTo(Counter o) {
        return Integer.compare(this.number, o.number);
    }
}
