package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.CounterRange;
import ar.edu.itba.pod.grpc.models.Flight;

import java.util.Map;

public class CheckInRepository {
    private Map<Flight, CounterRange> availableRangeForCheckIn;
}
