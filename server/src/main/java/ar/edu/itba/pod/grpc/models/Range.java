package ar.edu.itba.pod.grpc.models;

public abstract class Range {
    private final int start;
    private final int end;

    protected Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
