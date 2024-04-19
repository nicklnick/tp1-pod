package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.CounterRange;
import ar.edu.itba.pod.grpc.models.FreeRange;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AirportRepositoryImpl implements AirportRepository {
    private List<Counter> totalCounters;

    private Map<Sector,List<Counter>> countersBySector;

    private Map<Sector,List<FreeRange>> ranges;

    private Map<Sector,List<CounterRange>> onGoingAirlineRange;

    private Map<Sector, Queue<CounterRange>> pendingAirlineRange;


    @Override
    public void addSector(String name) {

    }

    @Override
    public void addCounterToSector(String sectorName, int number) {

    }

    @Override
    public void addRangeToSector(String sectorName, int start, int end) {

    }

    @Override
    public void assignCounterRangeToAirline(String sectorName, String airline, int start, int end) {

    }
}
