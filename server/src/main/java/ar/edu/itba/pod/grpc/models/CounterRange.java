package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class CounterRange  {
    private final int start;
    private final int end;
    private List<Counter> counters;
    private final Sector sector;
    private List<Flight> flights;
    private int queueSize;

    public CounterRange(int start, int end, Sector sector, List<Counter> counters, List<Flight> flights) {
        this.start = start;
        this.end = end;
        this.sector = sector;
        this.flights = flights;
        this.counters = counters;
        this.queueSize = 0;
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

    public int getQueueSize(){
        return queueSize;
    }

    public synchronized void addCounter(Counter counter) {
        counters.add(counter);
    }

    private synchronized void passengerStarted() {
        queueSize++;
    }
    private synchronized void passengerFinished() {
        queueSize--;
    }



}
