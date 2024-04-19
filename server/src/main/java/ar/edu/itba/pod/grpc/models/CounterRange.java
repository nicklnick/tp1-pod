package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CounterRange  {
    private final int start;
    private final int end;
    private final Airline airline;
    private final int totalCounters;
    private final List<Counter> counters;
    private final List<Flight> flights;
    private final Queue<Booking> passengers;

    public CounterRange(int start, int end, Airline airline, int totalCounters) {
        this.start = start;
        this.end = end;
        this.airline = airline;
        this.totalCounters = totalCounters;
        this.flights = new ArrayList<>();
        this.counters = new ArrayList<>();
        this.passengers = new LinkedBlockingQueue<>();
    }

    public synchronized void addFlight(Flight flight) {
        this.flights.add(flight);
    }
    public synchronized void addCounter(Counter counter) {
        counters.add(counter);
    }
    public synchronized void addPassenger(Booking booking) {
        passengers.add(booking);
    }
    public synchronized List<Flight> getFlights() {
        return flights;
    }
    public synchronized int getQueueSize() {
        return passengers.size();
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public Airline getAirline() {
        return airline;
    }
    public int getTotalCounters() {
        return totalCounters;
    }
    public List<Counter> getCounters() {
        return counters;
    }




}
