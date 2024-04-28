package ar.edu.itba.pod.grpc.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static NotificationData.Builder newBuilder() {
        return new NotificationData.Builder();
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

    public static class Builder {
        private NotificationType type;
        private Airline airline;
        private String sector;
        private List<Range> counterRange;
        private String booking;
        private List<String> flights;
        private int people;
        private int pendingsAhead;

        public Builder setType(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder setAirline(Airline airline) {
            this.airline = airline;
            return this;
        }

        public Builder setSector(Sector sector) {
            this.sector = sector.getName();
            return this;
        }

        public Builder setSectorName(String sectorName) {
            this.sector = sectorName;
            return this;
        }

        public Builder setCounterRange(List<Range> counterRange) {
            this.counterRange = counterRange;
            return this;
        }

        public Builder setCounterRange(Range counterRange) {
            this.counterRange = new ArrayList<>();
            this.counterRange.add(counterRange);
            return this;
        }

        public Builder setBooking(String booking) {
            this.booking = booking;
            return this;
        }

        public Builder setFlightCodes(List<String> flights) {
            this.flights = flights;
            return this;
        }

        public Builder setFlights(List<Flight> flights) {
            this.flights = flights.stream().map(Flight::getCode).collect(Collectors.toList());
            return this;
        }


        public Builder setPeople(int people) {
            this.people = people;
            return this;
        }

        public Builder setPendingsAhead(int pendingsAhead) {
            this.pendingsAhead = pendingsAhead;
            return this;
        }

        public NotificationData build() {
            return new NotificationData(type, airline, sector, counterRange, booking, flights, people, pendingsAhead);
        }

    }
}
