package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class ContiguousRange extends Range {

    private final List<Counter> counters = new ArrayList<>();
    private int occupiedCounters;

    public ContiguousRange(int start, int end, Sector sector) {
        super(start, end, sector);
        this.occupiedCounters = 0;
    }

    public synchronized void occupy(int amount) {
        if (occupiedCounters + amount > getEnd() - getStart()) {
            throw new IllegalArgumentException("Not enough space");
        }
        occupiedCounters += amount;
    }

    public List<Counter> getCounters() {
        return counters;
    }

    public void addAll(List<Counter> countersToAdd) {
        if (countersToAdd == null)
            return;
        this.counters.addAll(countersToAdd);
    }

    public void add(Counter counter) {
        if (counter == null)
            return;
        this.counters.add(counter);
    }

    public int getOccupied() {
        return occupiedCounters;
    }
}
