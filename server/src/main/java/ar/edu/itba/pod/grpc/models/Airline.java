package ar.edu.itba.pod.grpc.models;

public class Airline {

    private final String name;

    public Airline(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
