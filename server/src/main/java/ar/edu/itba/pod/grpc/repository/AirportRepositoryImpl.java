package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;

import java.util.*;

public class AirportRepositoryImpl implements AirportRepository {

    private static AirportRepositoryImpl instance;

    private Integer counterId = 1;
    private final List<Counter> totalCounters = new ArrayList<>();

    private final Map<Sector,List<Counter>> countersBySector = new HashMap<>();

    private final Map<Sector,List<FreeRange>> ranges = new HashMap<>();

    private final Map<Sector,List<CounterRange>> onGoingAirlineRange = new HashMap<>();

    private final Map<Sector, Queue<CounterRange>> pendingAirlineRange = new HashMap<>();


    private AirportRepositoryImpl() {
        throw new AssertionError("No se puede instanciar esta clase");
    }

    public synchronized static AirportRepositoryImpl getInstance() {
        if(instance == null) {
            instance = new AirportRepositoryImpl();
        }
        return instance;
    }

    @Override
    public synchronized void addSector(String name) {
        Sector sectorToAdd = new Sector(name);
        if(countersBySector.containsKey(sectorToAdd)) {
            throw new IllegalArgumentException("Ya existe un sector con el nombre indicado");
        }
        countersBySector.put(sectorToAdd, new ArrayList<>());
        ranges.put(sectorToAdd, new ArrayList<>());
        onGoingAirlineRange.put(sectorToAdd, new ArrayList<>());
        pendingAirlineRange.put(sectorToAdd, new LinkedList<>());
    }

    @Override
    public synchronized void addCountersToSector(String sectorName, int count) {
        Sector sector = new Sector(sectorName);
        if(!countersBySector.containsKey(sector)) {
            throw new IllegalArgumentException("No existe un sector con el nombre indicado");
        }
        if(count < 0) {
            throw new IllegalArgumentException("La cantidad de counters a agregar debe ser mayor a 0");
        }

        int trueCount = count;
        int newCounterId = counterId;
        int occupiedCounters = 0;
        List<Counter> currentCounters = null;
        if(ranges.containsKey(sector)) {

            FreeRange oldRange = ranges.get(sector).getLast();
            if(oldRange.getEnd() == counterId - 1) {
                occupiedCounters = ranges.get(sector).getLast().getOccupied();
                currentCounters = ranges.get(sector).getLast().getCounters();
                ranges.get(sector).removeLast();
                newCounterId = oldRange.getStart();
                count += oldRange.getEnd() - oldRange.getStart() + 1;


            }
        }
        FreeRange freeRange = new FreeRange(newCounterId, newCounterId + count - 1);
        freeRange.addAll(currentCounters);
        ranges.get(sector).add(freeRange);
        for(int i = 0 ; i < trueCount ; i++) {
            Counter counterToAdd = new Counter(counterId, Status.PENDING);
            totalCounters.add(counterToAdd);
            countersBySector.get(sector).add(counterToAdd);
            ranges.get(sector).getLast().add(counterToAdd);
            counterId++;
        }
    }

    @Override
    public void addRangeToSector(String sectorName, int start, int end) {


    }

    @Override
    public void assignCounterRangeToAirline(String sectorName, String airline, int start, int end) {

    }
}
