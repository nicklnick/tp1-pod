package ar.edu.itba.pod.grpc.models;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Airport {
    ConcurrentMap<Sector, List<Counter>> sectors = new ConcurrentHashMap<>();
}
