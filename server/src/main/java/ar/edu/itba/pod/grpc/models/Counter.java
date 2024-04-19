package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class Counter implements Comparable<Counter> {

    private final int number;

    private final Status status;

    public Counter(int number, Status status) {
        this.number = number;
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int compareTo(Counter o) {
        return Integer.compare(this.number, o.number);
    }
}
