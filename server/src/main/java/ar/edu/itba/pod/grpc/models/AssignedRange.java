package ar.edu.itba.pod.grpc.models;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class AssignedRange extends Range {
    private final Airline airline;
    private final int totalCounters;
    private final Map<Counter, Integer> counters;
    private final List<Flight> flights;
    private final Queue<Booking> passengers;


    public AssignedRange(int start, int end, Sector sector, Airline airline, int totalCounters) {
        super(start, end, sector);
        this.airline = airline;
        this.totalCounters = totalCounters;
        this.flights = new ArrayList<>();
        this.counters = new HashMap<>();
        this.passengers = new LinkedBlockingQueue<>();
    }

    public AssignedRange(Sector sector, Airline airline, int totalCounters) {
        super(0, 0, sector);
        this.airline = airline;
        this.totalCounters = totalCounters;
        this.flights = new ArrayList<>();
        this.counters = new HashMap<>();
        this.passengers = new LinkedBlockingQueue<>();
    }

    public void addFlight(Flight flight) {
        this.flights.add(flight);
    }

    public void addCounter(Counter counter) {
        counters.putIfAbsent(counter,0);
    }

    public void addPassenger(Booking booking) {
        passengers.add(booking);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public int getQueueSize() {
        return passengers.size();
    }

    public Queue<Booking> getPassengers() {
        return passengers;
    }

    public Airline getAirline() {
        return airline;
    }

    public int getTotalCounters() {
        return totalCounters;
    }

    public List<Counter> getCounters() {
        return counters.keySet().stream().toList();
    }
    public Map<Counter, Integer> getCountersMap() {
        return counters;
    }
}
