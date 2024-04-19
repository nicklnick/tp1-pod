package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.CounterRange;
import ar.edu.itba.pod.grpc.models.FreeRange;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;
import java.util.Map;

public class AirportRepository {
    private List<Counter> totalCounters;

    private Map<Sector,List<Counter>> countersBySector;

    private Map<Sector,List<FreeRange>> ranges;

    private Map<Sector,List<CounterRange>> onGoing;

    private Map<Sector,List<CounterRange>> pending;

}
