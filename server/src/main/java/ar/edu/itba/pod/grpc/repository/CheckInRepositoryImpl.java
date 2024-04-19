package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.CounterRange;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;

import java.util.Map;

public class CheckInRepositoryImpl implements CheckInRepository {
    private Map<Flight, CounterRange> availableRangeForCheckIn;

    @Override
    public void addCounterRangeToFlight(String flight, String sectorName, int start, int end) {

    }
}
