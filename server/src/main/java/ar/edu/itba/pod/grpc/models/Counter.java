package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class Counter implements Comparable<Counter> {

    private final int number;

    private Status status;

    public Counter(int number, Status status) {
        this.number = number;
        this.status = status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
    public int getNumber() {
        return number;
    }
    @Override
    public int compareTo(Counter o) {
        return Integer.compare(this.number, o.number);
    }
}
