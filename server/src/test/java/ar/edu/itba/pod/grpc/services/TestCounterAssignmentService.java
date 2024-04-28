package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class TestCounterAssignmentService {

    private Sector sectorA;
    private Sector sectorB;

    private Airline american;

    private Airline aerolineas;

    private List<Flight> flightsAerolineas;
    private List<Flight> flightsAerolineasTwo;
    private List<Flight> flightsAmerican;

    private SectorServiceImpl sectorService;

    @Before
    public void setUp() {
        sectorA = new Sector("A");
        sectorB = new Sector("B");
        american = new Airline("American Airlines");
        aerolineas = new Airline("Aerolineas Argentinas");

        flightsAmerican = new ArrayList<>();
        flightsAmerican.add(new Flight(american, "AM100"));
        flightsAmerican.add(new Flight(american, "AM200"));
        flightsAmerican.add(new Flight(american, "AM300"));

        flightsAerolineas = new ArrayList<>();
        flightsAerolineas.add(new Flight(aerolineas, "AA100"));
        flightsAerolineas.add(new Flight(aerolineas, "AA200"));

        flightsAerolineasTwo = new ArrayList<>();
        flightsAerolineasTwo.add(new Flight(aerolineas, "AA300"));
        flightsAerolineasTwo.add(new Flight(aerolineas, "AA400"));

        sectorService = new SectorServiceImpl();
        sectorService.addSector("A");
        sectorService.addSector("B");
        PassengerServiceImpl passengerService = new PassengerServiceImpl();

        for (int i = 0; i < flightsAerolineas.size(); i++) {
            passengerService.addExpectedPassenger(new Booking("P" + i + "AA"), flightsAerolineas.get(i));
        }

        for (int i = 0; i < flightsAmerican.size(); i++) {
            passengerService.addExpectedPassenger(new Booking("P" + i + "AM"), flightsAmerican.get(i));
        }

        for (int i = 0; i < flightsAerolineasTwo.size(); i++) {
            passengerService.addExpectedPassenger(new Booking("P" + i + "AA2"), flightsAerolineasTwo.get(i));
        }
    }

    @Test
    public void testGetRangesBySectorEmptyRange() {
        //Test empty range
        Assert.assertThrows(IllegalArgumentException.class, () -> sectorService.getRangesBySector(sectorA, 0, 20));
    }

    @Test
    public void testGetRangesBySectorResultsInRange() {

        sectorService.addCountersToSector(sectorA, 20);
        sectorService.addCountersToSector(sectorB, 20);
        sectorService.addCountersToSector(sectorA, 10);

        List<Range> ranges = sectorService.getRangesBySector(sectorA, 1, 20);
        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 20);
        Assertions.assertEquals(1, ranges.size());

        //Just in the limit
        ranges = sectorService.getRangesBySector(sectorA, 20, 20);
        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 20);
        Assertions.assertEquals(1, ranges.size());

        ranges = sectorService.getRangesBySector(sectorA, 1, 1);
        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 20);
        Assertions.assertEquals(1, ranges.size());

        //Nothing included
        Assertions.assertThrows(IllegalArgumentException.class, () -> sectorService.getRangesBySector(sectorA, 21, 22));

        //The second range is included
        ranges = sectorService.getRangesBySector(sectorA, 21, 41);
        Assertions.assertEquals(ranges.get(0).getStart(), 41);
        Assertions.assertEquals(ranges.get(0).getEnd(), 50);
        Assertions.assertEquals(1, ranges.size());

        //Both included
        ranges = sectorService.getRangesBySector(sectorA, 20, 41);
        //FIRST
        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 20);
        //SECOND
        Assertions.assertEquals(ranges.get(1).getStart(), 41);
        Assertions.assertEquals(ranges.get(1).getEnd(), 50);

        Assertions.assertEquals(2, ranges.size());

        //Both included
        ranges = sectorService.getRangesBySector(sectorA, 1, 50);
        //FIRST
        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 20);
        //SECOND
        Assertions.assertEquals(ranges.get(1).getStart(), 41);
        Assertions.assertEquals(ranges.get(1).getEnd(), 50);

        Assertions.assertEquals(2, ranges.size());

    }

    @Test
    public void testGetRangesBySectorIncludeAssignedRanges() {
        sectorService.addCountersToSector(sectorA, 20);
        sectorService.addCountersToSector(sectorB, 20);
        sectorService.addCountersToSector(sectorA, 15);

        sectorService.assignCounterRangeToAirline(sectorA, american, flightsAmerican, 5);
        sectorService.assignCounterRangeToAirline(sectorA, aerolineas, flightsAerolineas, 5);

        List<Range> ranges = sectorService.getRangesBySector(sectorA, 1, 20);

        Assertions.assertEquals(ranges.size(), 3);

        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 5);

        Assertions.assertEquals(ranges.get(1).getStart(), 6);
        Assertions.assertEquals(ranges.get(1).getEnd(), 10);

        Assertions.assertEquals(ranges.get(2).getStart(), 11);
        Assertions.assertEquals(ranges.get(2).getEnd(), 20);
        Assertions.assertEquals(ranges.get(2).getClass(), ContiguousRange.class);

        //Remove first one, now should include an empty range

        sectorService.freeAssignedRange(sectorA, american, 1);
        ranges = sectorService.getRangesBySector(sectorA, 1, 20);
        Assertions.assertEquals(ranges.size(), 3);

        Assertions.assertEquals(ranges.get(0).getStart(), 1);
        Assertions.assertEquals(ranges.get(0).getEnd(), 5);
        Assertions.assertEquals(ranges.get(0).getClass(), ContiguousRange.class);

        Assertions.assertEquals(ranges.get(1).getStart(), 6);
        Assertions.assertEquals(ranges.get(1).getEnd(), 10);

        Assertions.assertEquals(ranges.get(2).getStart(), 11);
        Assertions.assertEquals(ranges.get(2).getEnd(), 20);
        Assertions.assertEquals(ranges.get(2).getClass(), ContiguousRange.class);

        //Add an assigned range to second continuous range in sectorA. Should not be included
        sectorService.assignCounterRangeToAirline(sectorA, aerolineas, flightsAerolineasTwo, 15);
        ranges = sectorService.getRangesBySector(sectorA, 1, 41);
        Assertions.assertEquals(ranges.size(), 4);

    }
}
