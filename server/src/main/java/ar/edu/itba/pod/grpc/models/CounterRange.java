package ar.edu.itba.pod.grpc.models;

import java.util.List;
import java.util.Queue;

public class CounterRange  {
    private final int start;
    private final int end;
    private List<Counter> counters;
    private List<Flight> flights;

    private Airline airline;
    private int totalCounters;
    private Queue<Booking> passengers;

    public CounterRange(int start, int end, List<Counter> counters, List<Flight> flights, Airline airline, int totalCounters, Queue<Booking> passengers) {
        this.start = start;
        this.end = end;
        this.flights = flights;
        this.counters = counters;
        this.airline = airline;
        this.totalCounters = totalCounters;
        this.passengers = passengers;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    public synchronized void addFlight(Flight flight) {
        this.flights.add(flight);
    }
    public synchronized List<Flight> getFlights() {
        return flights;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }

    public synchronized void addCounter(Counter counter) {
        counters.add(counter);
    }


}
