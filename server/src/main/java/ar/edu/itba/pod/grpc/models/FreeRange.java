package ar.edu.itba.pod.grpc.models;

public class FreeRange {

    private final int start;

    private final int end;

    private int occupied;

    public FreeRange(int start, int end) {
        this.start = start;
        this.end = end;
        this.occupied = 0;
    }

    public synchronized void occupy(int amount) {
        if(occupied + amount > end - start) {
            throw new IllegalArgumentException("Not enough space");
        }
        occupied += amount;
    }

    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int getOccupied() {
        return occupied;
    }
}
