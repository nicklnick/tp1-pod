package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class FreeRange {

    private final int start;

    private final int end;

    private int occupiedCounters;
    private final List<Counter> counters = new ArrayList<>();

    public FreeRange(int start, int end) {
        this.start = start;
        this.end = end;
        this.occupiedCounters = 0;
    }

    public synchronized void occupy(int amount) {
        if(occupiedCounters + amount > end - start) {
            throw new IllegalArgumentException("Not enough space");
        }
        occupiedCounters += amount;
    }

    public List<Counter> getCounters() {
        return counters;
    }
    public void addAll(List<Counter> countersToAdd) {
        if(countersToAdd == null)
            return;
        this.counters.addAll(countersToAdd);
    }
    public void add(Counter counter) {
        if (counter == null)
            return;
        this.counters.add(counter);
    }

    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int getOccupied() {
        return occupiedCounters;
    }
}
