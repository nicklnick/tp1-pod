package ar.edu.itba.pod.grpc.models;

public class Counter implements Comparable<Counter> {

    private final int number;
    private CounterStatus status;


    public Counter(int number, CounterStatus status) {
        this.number = number;
        this.status = status;
    }

    public CounterStatus getStatus() {
        return status;
    }

    public void setStatus(CounterStatus status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int compareTo(Counter o) {
        return Integer.compare(this.number, o.number);
    }
}
