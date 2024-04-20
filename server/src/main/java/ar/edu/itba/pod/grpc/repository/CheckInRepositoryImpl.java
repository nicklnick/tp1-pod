package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;

import java.util.Map;
import java.util.Optional;

public class CheckInRepositoryImpl implements CheckInRepository {

    private static CheckInRepositoryImpl instance;
    private final Map<Flight, AssignedRange> availableRangeForCheckIn;
    private final AirportRepository airportRepository = AirportRepositoryImpl.getInstance();
    private final PassengerRepository passengerRepository = PassengersRepositoryImpl.getInstance();


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
    public void counterCheckIn(String sectorName, int rangeId, String airlineName) {
        Sector sector = new Sector(sectorName);
        Airline airline = new Airline(airlineName);
        Optional<AssignedRange> maybeAssignedRange;
        // ---- casos de error ----
        if(!airportRepository.containsSector(sector)) {
            throw new IllegalArgumentException("No existe un sector con el nombre indicado");
        }
        maybeAssignedRange = airportRepository.searchAssignedRangeForAirline(airportRepository.getSectorsCounterRange().get(sector), rangeId, airline);
        if(maybeAssignedRange.isEmpty()) {
            throw new IllegalArgumentException("No se encontró un rango asignado al que pertenezca el numero de rango indicado o no pertenece a la aerolinea indicada");
        }
        // ---- fin de casos de error ----
        AssignedRange assignedRange = maybeAssignedRange.get();
        if(assignedRange.getQueueSize() == 0) {
            return;
        }

        // busco mostrador vacio
        for(Counter counter : assignedRange.getCounters()) {
            if (counter.getStatus() == CounterStatus.READY) {
                synchronized (counter) {
                //si encuentro, cambio su estado a ocupado
                    counter.setStatus(CounterStatus.BUSY);
                    Booking passenger = assignedRange.getPassengers().poll();
                    passengerRepository.getPassengerStatus().put(passenger, PassengerStatus.CHECKED_IN);
                    counter.setStatus(CounterStatus.READY);
                    return;
                }
            }
        }
    }
}
