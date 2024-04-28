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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return start == range.start && end == range.end && sector.equals(range.sector);
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
