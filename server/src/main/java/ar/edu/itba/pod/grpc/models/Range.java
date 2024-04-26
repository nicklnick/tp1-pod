package ar.edu.itba.pod.grpc.models;

public abstract class Range {
    private final int start;
    private final int end;

    private final Sector sector;

    protected Range(int start, int end, Sector sector) {
        this.start = start;
        this.end = end;
        this.sector = sector;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
    public Sector getSector() {
        return sector;
    }
}
