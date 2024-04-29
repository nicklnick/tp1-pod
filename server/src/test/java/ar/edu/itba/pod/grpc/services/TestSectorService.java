package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import ar.edu.itba.pod.grpc.utils.Triple;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestSectorService {
    private final SectorService sectorService = new SectorServiceImpl();
    private final PassengerService passengerService = new PassengerServiceImpl();

    @Test
    public void addSectors() {
        sectorService.addSector("A");
        sectorService.addSector("B");
        sectorService.addSector("C");

        Map<Sector, List<Counter>> sectorsMap = sectorService.listSectors();

        List<Sector> expected = new ArrayList<>();
        expected.add(new Sector("A"));
        expected.add(new Sector("B"));
        expected.add(new Sector("C"));

        List<Sector> sectors = sectorsMap.keySet().stream().toList();
        Assert.assertTrue(sectors.containsAll(expected));
        Assert.assertEquals(sectors.size(), expected.size());
    }

    @Test
    public void addCountersToSector() {
        addSectors();

        List<Map.Entry<String, Integer>> sectorsCountersCount = new ArrayList<>();

        sectorsCountersCount.add(Map.entry("A", 10));
        sectorsCountersCount.add(Map.entry("B", 2));
        sectorsCountersCount.add(Map.entry("C", 1));
        sectorsCountersCount.add(Map.entry("A", 5));

        for (Map.Entry<String, Integer> sectorCountersCount:
                sectorsCountersCount) {
            Sector sector = new Sector(sectorCountersCount.getKey());
            sectorService.addCountersToSector(sector, sectorCountersCount.getValue());
        }

        Map<Sector, List<ContiguousRange>> sectorsCounterRanges = sectorService.getContiguousRanges();

        // Sector A counter ranges
        Sector sectorA = new Sector("A");
        List<ContiguousRange> sectorARanges = sectorsCounterRanges.get(sectorA);
        Assert.assertNotEquals(sectorARanges, null);
        Assert.assertEquals(sectorARanges.size(), 2);

        ContiguousRange sectorAFirstRange = sectorARanges.get(0);
        Assert.assertEquals(sectorAFirstRange.getStart(), 1);
        Assert.assertEquals(sectorAFirstRange.getEnd(), 10);

        ContiguousRange sectorASecondRange = sectorARanges.get(1);
        Assert.assertEquals(sectorASecondRange.getStart(), 14);
        Assert.assertEquals(sectorASecondRange.getEnd(), 18);

        // Sector B counter ranges
        Sector sectorB = new Sector("B");
        List<ContiguousRange> sectorBRanges = sectorsCounterRanges.get(sectorB);
        Assert.assertNotEquals(sectorBRanges, null);
        Assert.assertEquals(sectorBRanges.size(), 1);

        ContiguousRange sectorBFirstRange = sectorBRanges.get(0);
        Assert.assertEquals(sectorBFirstRange.getStart(), 11);
        Assert.assertEquals(sectorBFirstRange.getEnd(), 12);

        // Sector C counter ranges
        Sector sectorC = new Sector("C");
        List<ContiguousRange> sectorCRanges = sectorsCounterRanges.get(sectorC);
        Assert.assertNotEquals(sectorCRanges, null);
        Assert.assertEquals(sectorCRanges.size(), 1);

        ContiguousRange sectorCFirstRange = sectorCRanges.get(0);
        Assert.assertEquals(sectorCFirstRange.getStart(), 13);
        Assert.assertEquals(sectorCFirstRange.getEnd(), 13);
    }

    @Test
    public void assignCounterRangeToAirline() {
        addCountersToSector();

        Sector sectorA = new Sector("A");
        Sector sectorB = new Sector("B");

        List<Triple<String, String, String>> passengersData = new ArrayList<>();
        passengersData.add(new Triple<>("X100", "AA100", "AirlineA"));
        passengersData.add(new Triple<>("X101", "AA101", "AirlineA"));
        passengersData.add(new Triple<>("X102", "AA102", "AirlineA"));
        passengersData.add(new Triple<>("Y200", "BB200", "AirlineB"));
        passengersData.add(new Triple<>("Y201", "BB201", "AirlineB"));

        // Create entities
        for (Triple<String, String, String> passengerData : passengersData) {
            Booking booking = new Booking(passengerData.getFirst());
            Airline airline = new Airline(passengerData.getThird());
            Flight flight = new Flight(airline, passengerData.getSecond());

            passengerService.addExpectedPassenger(booking, flight);
        }

        Airline airlineA = new Airline("AirlineA");

        List<Flight> airlineAFlights = new ArrayList<>();
        airlineAFlights.add(new Flight(airlineA, "AA100"));
        airlineAFlights.add(new Flight(airlineA, "AA101"));
        airlineAFlights.add(new Flight(airlineA, "AA102"));

        Airline airlineB = new Airline("AirlineB");

        List<Flight> airlineBFlights = new ArrayList<>();
        airlineBFlights.add(new Flight(airlineB, "BB200"));
        airlineBFlights.add(new Flight(airlineB, "BB201"));

        sectorService.assignCounterRangeToAirline(sectorA, airlineA, airlineAFlights, 3);
        sectorService.assignCounterRangeToAirline(sectorB, airlineB, airlineBFlights, 2);
    }
}
