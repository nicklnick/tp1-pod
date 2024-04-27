package ar.edu.itba.pod.grpc.models;

import java.util.List;

public class NotificationData {
    private final NotificationType type;
    private final Airline airline;
    private final String sector;
    private final List<Range> counterRange;
    private final String booking;
    private final List<String> flights;
    private final int people;
    private final int pendingsAhead;

    public NotificationData(NotificationType type, Airline airline, String sector, List<Range> counterRange, String booking, List<String> flights, int people, int pendingsAhead) {
        this.type = type;
        this.airline = airline;
        this.sector = sector;
        this.counterRange = counterRange;
        this.booking = booking;
        this.flights = flights;
        this.people = people;
        this.pendingsAhead = pendingsAhead;
    }

    public NotificationData(NotificationType type, Airline airline) {
        this.type = type;
        this.airline = airline;
        this.sector = null;
        this.counterRange = null;
        this.booking = null;
        this.flights = null;
        this.people = 0;
        this.pendingsAhead = 0;
    }

    public NotificationType getType() {
        return type;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getSector() {
        return sector;
    }

    public List<Range> getCounterRange() {
        return counterRange;
    }

    public String getBooking() {
        return booking;
    }

    public List<String> getFlights() {
        return flights;
    }

    public int getPeople() {
        return people;
    }

    public int getPendingsAhead() {
        return pendingsAhead;
    }
}
