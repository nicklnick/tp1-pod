package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HistoryService {
    void addCheckIn(CheckIn checkIn);

    List<CheckIn> getSectorCheckInHistory(Optional<Sector> maybeSector);

    List<CheckIn> getAirlineCheckInHistory(Optional<Airline> maybeAirline);

    List<CheckIn> getCounterCheckInHistory(Optional<Counter> maybeCounter);

    List<CheckIn> getAllCheckIns();
    CheckIn getPassengerCheckIn(Optional<Booking> maybePassenger);

    List<AssignedRange> getAssignedRangesHistory();

    void addAssignedRange(AssignedRange assignedRange);

    boolean airlineHasStartedCheckInOnFlights(Airline airline, List<Flight> flights);
}
