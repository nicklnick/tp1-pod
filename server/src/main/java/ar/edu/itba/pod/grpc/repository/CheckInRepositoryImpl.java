package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.CounterRange;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;

import java.util.Map;

public class CheckInRepositoryImpl implements CheckInRepository {

    private static CheckInRepositoryImpl instance;
    private Map<Flight, CounterRange> availableRangeForCheckIn;



    private CheckInRepositoryImpl() {
        throw new AssertionError("No se puede instanciar esta clase");
    }
    public synchronized static CheckInRepositoryImpl getInstance() {
        if(instance == null) {
            instance = new CheckInRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addCounterRangeToFlight(String flight, String sectorName, int start, int end) {

    }
}
