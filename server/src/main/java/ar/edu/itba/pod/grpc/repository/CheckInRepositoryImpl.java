package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.NotificationsServiceImpl;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CheckInRepositoryImpl implements CheckInRepository {

    private static CheckInRepositoryImpl instance;
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();
    private final NotificationsService notificationsService = new NotificationsServiceImpl();
    private final Map<Flight, AssignedRange> availableRangeForCheckIn = new HashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private CheckInRepositoryImpl() {
    }

    public synchronized static CheckInRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new CheckInRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Optional<CheckIn> counterCheckIn(AssignedRange assignedRange) {
        // Search for empty counter
        for (Counter counter : assignedRange.getCounters()) {
            if (counter.getStatus() == CounterStatus.READY_FOR_CHECKIN) {
                    counter.setStatus(CounterStatus.BUSY);

                    final Booking passenger = assignedRange.getPassengers().poll();
                    passengerService.changePassengerStatus(passenger, PassengerStatus.FINISHED_CHECKIN);

                    if (notificationsService.isRegisteredForNotifications(assignedRange.getAirline())) {
                        NotificationData notification = NotificationData.newBuilder()
                                .setType(NotificationType.NOTIFICATION_PASSENGER_COMPLETED_CHECKIN)
                                .setBooking(passenger)
                                .setCounterRange(new ContiguousRange(
                                        counter.getNumber(),
                                        counter.getNumber(),
                                        assignedRange.getSector()
                                ))
                                .setSector(assignedRange.getSector())
                                .build();

                        notificationsService.sendNotification(notification);
                    }

                    Flight flight = passengerService.listExpectedPassengers().get(passenger);
                    CheckIn checkIn = new CheckIn(assignedRange.getSector(), counter, assignedRange.getAirline(), flight, passenger, assignedRange);
                    historyService.addCheckIn(checkIn);
                    counter.setStatus(CounterStatus.READY_FOR_CHECKIN);

                    return Optional.of(checkIn);
            }
        }
        return Optional.empty();
    }

    @Override
    public AssignedRange getAvailableRangeForCheckIn(Flight flight) {
        readWriteLock.readLock().lock();
        try {
            return availableRangeForCheckIn.get(flight);
        } finally {
           readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void addAvailableRangeForFlight(Flight flight, AssignedRange assignedRange) {
        readWriteLock.writeLock().lock();
        try {
            availableRangeForCheckIn.put(flight, assignedRange);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
