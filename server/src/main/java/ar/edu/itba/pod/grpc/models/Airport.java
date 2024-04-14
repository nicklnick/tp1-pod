package ar.edu.itba.pod.grpc.models;

import java.util.*;

public class Airport {

    private final Map<Sector, List<Counter>> sectors = new HashMap<>();
    private final Set<Passenger> expectedPassengers = new HashSet<>();
    private final Set<Flight> flights = new HashSet<>();

    private int lastCounterNumber = 1;

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
            Counter counter = new Counter(sector, i);
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
}
