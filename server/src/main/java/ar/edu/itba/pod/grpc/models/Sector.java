package ar.edu.itba.pod.grpc.models;

public class Sector implements Comparable<Sector> {

    private final String name;

    public Sector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Sector o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
