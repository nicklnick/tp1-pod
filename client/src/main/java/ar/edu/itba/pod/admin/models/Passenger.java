package ar.edu.itba.pod.admin.models;

import com.opencsv.bean.CsvBindByPosition;

public class Passenger {

    @CsvBindByPosition(position = 0)
    private String bookingCode;
    @CsvBindByPosition(position = 1)
    private String flightCode;
    @CsvBindByPosition(position = 2)
    private String airlineName;

    public String getBookingCode() {
        return bookingCode;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "bookingCode='" + bookingCode + '\'' +
                ", flightCode='" + flightCode + '\'' +
                ", airlineName='" + airlineName + '\'' +
                '}';
    }
}
