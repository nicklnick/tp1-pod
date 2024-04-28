package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.HistoryRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistoryServiceImpl implements HistoryService {
    private static final HistoryRepository historyRepo = HistoryRepositoryImpl.getInstance();


    @Override
    public void addCheckIn(CheckIn checkIn) {
        if (historyRepo.passangerDidCheckin(checkIn.getBooking())) {
            throw new IllegalArgumentException("Passenger already did checkin");
        }

        historyRepo.addCheckIn(checkIn);
    }

    @Override
    public List<CheckIn> getSectorCheckInHistory(Optional<Sector> maybeSector) {
        return maybeSector.map(historyRepo::getSectorCheckInHistory).orElse(null);
    }
    @Override
    public List<CheckIn> getAirlineCheckInHistory(Optional<Airline> maybeAirline) {
        return maybeAirline.map(historyRepo::getAirlineCheckInHistory).orElse(null);
    }
    @Override
    public List<CheckIn> getCounterCheckInHistory(Optional<Counter> maybeCounter) {
        return maybeCounter.map(historyRepo::getCounterCheckInHistory).orElse(null);
    }

    @Override
    public CheckIn getPassengerCheckIn(Optional<Booking> maybePassenger) {
        return maybePassenger.map(historyRepo::getPassengerCheckIn).orElse(null);
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        return historyRepo.getAllCheckIns();
    }

    @Override
    public List<AssignedRange> getAssignedRangesHistory() {
        return historyRepo.getAssignedRangesHistory();
    }

    @Override
    public void addAssignedRange(AssignedRange assignedRange) {
        historyRepo.addAssignedRange(assignedRange);
    }
}
