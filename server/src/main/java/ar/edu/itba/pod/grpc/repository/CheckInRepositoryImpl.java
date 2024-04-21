package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.HashMap;
import java.util.Map;

public class CheckInRepositoryImpl implements CheckInRepository {

    private static CheckInRepositoryImpl instance;

    private final PassengerService passengerService = new PassengerServiceImpl();

    private final Map<Flight, AssignedRange> availableRangeForCheckIn = new HashMap<>();


    private CheckInRepositoryImpl() {
    }

    public synchronized static CheckInRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new CheckInRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void counterCheckIn(AssignedRange assignedRange) {
        // busco mostrador vacio
        for (Counter counter : assignedRange.getCounters()) {
            if (counter.getStatus() == CounterStatus.READY) {
                // TODO: arreglar, el synchronized va a agarrar instancias distintas de la misma cosa
                synchronized (counter) {
                    // si encuentro, cambio su estado a ocupado
                    counter.setStatus(CounterStatus.BUSY);

                    final Booking passenger = assignedRange.getPassengers().poll();
                    passengerService.changePassengerStatus(passenger, PassengerStatus.CHECKED_IN);

                    counter.setStatus(CounterStatus.READY); // TODO: creo que vamos a tener problemas porque esto es un puntero distinto al posta no?

                    return;
                }
            }
        }
    }

    @Override
    public AssignedRange getAvailableRangeForCheckIn(Booking booking) {
        final Flight flight = passengerService.listExpectedPassengers().get(booking);
        return availableRangeForCheckIn.get(flight);
    }

}
