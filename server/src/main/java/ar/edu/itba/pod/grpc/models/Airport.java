package ar.edu.itba.pod.grpc.models;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class Airport {

    private static Airport instance;
    private final Map<Sector, List<Counter>> sectors = new HashMap<>();
    private final Set<Passenger> expectedPassengers = new HashSet<>();
    private final Set<Flight> flights = new HashSet<>();
    private final Map<Airline, List<CounterRange>> airlineCounters = new HashMap<>();


    private int lastCounterNumber = 1;

    private Airport() {
    }

    public synchronized static Airport getInstance(){
        if (instance == null) {
            instance = new Airport();
        }

        return instance;
    }

    public void addSector(Sector sector) throws IllegalArgumentException {
        if (sectors.containsKey(sector))
            throw new IllegalArgumentException();

        sectors.putIfAbsent(sector, new ArrayList<>());
    }


    public synchronized void addCountersRange(Sector sector, int countersCount) throws IllegalArgumentException {
        if (!sectors.containsKey(sector) || countersCount < 0)
            throw new IllegalArgumentException();

        List<Counter> sectorCounters = sectors.get(sector);

        for (int i = lastCounterNumber; i <= (lastCounterNumber + countersCount); i++) {
            Counter counter = new Counter(/*sector,*/ i);
            sectorCounters.add(counter);
        }

        lastCounterNumber += countersCount;
    }

    public synchronized void addExpectedPassenger(Passenger passenger) throws IllegalArgumentException {
        if (expectedPassengers.contains(passenger))
            throw new IllegalArgumentException();

        if (flights.contains(passenger.getBooking().getFlight())) {
            Flight passengerFlight = passenger.getBooking().getFlight();

            for (Flight flight : flights) {
                if (passengerFlight.getCode().equals(flight.getCode()) &&
                        !passengerFlight.getAirline().equals(flight.getAirline()))
                    throw new IllegalArgumentException();
            }
        }

        flights.add(passenger.getBooking().getFlight());
        expectedPassengers.add(passenger);
    }

    public Map<Sector, List<Counter>> getCountersBySector() {
        return sectors;
    }

    public List<Counter> getCountersBySector(Sector sector){
        return  sectors.get(sector);
    }


    public CounterRange assignCounters(String airlineName, List<String> flights, String sectorName, int counterAmount) {
        if(counterAmount < 1 || airlineName == null || flights == null || sectorName == null) {
            throw new IllegalArgumentException();
        }
        Airline airline = new Airline(airlineName);
        airlineCounters.putIfAbsent(airline, new ArrayList<>());
        Sector sector = new Sector(sectorName);
        List<Counter> sectorCounters = sectors.get(sector);
        if(sectorCounters.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int count = 0;
        List<Counter> toAdd = new ArrayList<>();
        for(Counter counter : sectorCounters) {
            if(!counter.isAssignedToRange()) {
                count++;
                toAdd.add(counter);
                if(count == counterAmount) {
                    break;
                }
            } else {
                count = 0;
                toAdd.clear();
            }
        }
        int start , end;
        if(toAdd.isEmpty()){
            start = end = 0;
        } else {
            start = toAdd.get(0).getNumber();
            end = toAdd.get(toAdd.size() - 1).getNumber();
        }

        List<Flight> flightsToAdd = new ArrayList<>();
        for(String flightName : flights) {
            flightsToAdd.add(new Flight(airline, flightName));
        }

        CounterRange counterRange = new CounterRange(start, end, sector, toAdd, flightsToAdd);
        for(Counter counter : toAdd) {
            counter.setCounterRange(counterRange);
        }
        airlineCounters.get(airline).add(counterRange);

        return counterRange;
    }

    public CounterRange getCounterRangeToFree(int fromVal, Sector sector){

        List<Counter> counters = Optional.ofNullable(sectors.get(sector)).orElseThrow(IllegalArgumentException::new); //TODO: Manage exception;
        Counter first = null;
        for (Counter counter: counters){
            if(counter.getNumber() == fromVal){
                first = counter;
                break;
            }
        }

        if(first == null){
            return null;
        }

        return first.getCounterRange();
    }





}
