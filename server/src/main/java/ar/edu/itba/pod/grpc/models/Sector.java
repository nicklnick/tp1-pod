package ar.edu.itba.pod.grpc.models;

import java.util.Objects;

public class Sector implements Comparable<Sector> {

    private final String name;

    public Sector(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Sector o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sector sector = (Sector) o;
        return name.equals(sector.name);
    }

    public String getName() {
        return name;
    }
}
