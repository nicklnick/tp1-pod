package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;

public class Counter implements Comparable<Counter> {

    private final int number;
    private Boolean isBusy;

    private CounterRange counterRange;

    public Counter(int number) {
        this.number = number;
        isBusy = false;
    }

    public int getNumber() {
        return number;
    }

    public synchronized Boolean getIsBusy() {
        return isBusy;
    }
    public synchronized void setIsBusy(Boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void setCounterRange(CounterRange counterRange) {
        this.counterRange = counterRange;
    }

    public CounterRange getCounterRange() {
        return counterRange;
    }

    public boolean isAssignedToRange(){
        return counterRange !=null;
    }

    public void freeCounter() {
        this.counterRange = null;
        this.isBusy = false;
    }
    @Override
    public int compareTo(Counter o) {
        return Integer.compare(this.number, o.number);
    }
}
