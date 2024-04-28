package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;
import ar.edu.itba.pod.grpc.models.NotificationType;
import ar.edu.itba.pod.grpc.repository.NotificationsRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.NotificationsRepository;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;

import java.util.concurrent.BlockingQueue;

public class NotificationsServiceImpl implements NotificationsService {
    private static final NotificationsRepository notificationRepository = NotificationsRepositoryImpl.getInstance();
    private final PassengerService passengerService = new PassengerServiceImpl();
    private final Object notificationsLock = "notificationsLock";
    @Override
    public BlockingQueue<NotificationData> registerForNotifications(Airline airline) {
        if (notificationRepository.isRegisteredForNotifications(airline)) {
            throw new IllegalArgumentException("Airline is already registered for notifications");
        }

        BlockingQueue<NotificationData> notificationQueue;
        synchronized (notificationsLock) {
            notificationQueue = notificationRepository.registerForNotifications(airline);
            notificationRepository.sendNotification(new NotificationData(NotificationType.NOTIFICATION_REGISTER, airline));
        }
        return notificationQueue;
    }

    @Override
    public void unregisterForNotifications(Airline airline) throws IllegalArgumentException {
        if (!notificationRepository.isRegisteredForNotifications(airline)) {
            throw new IllegalArgumentException("Airline is not registered for notifications");
        }

        if (!passengerService.existsExpectedPassengerFromAirline(airline)) {
            throw new IllegalArgumentException("There are no expected passengers waiting for this airline");
        }

        synchronized (notificationsLock) {
            notificationRepository.sendNotification(new NotificationData(NotificationType.NOTIFICATION_UNREGISTER, airline));
            notificationRepository.unregisterForNotifications(airline);
        }
    }

    @Override
    public void sendNotification(NotificationData notificationData) throws IllegalArgumentException {
        if (!notificationRepository.isRegisteredForNotifications(notificationData.getAirline())) {
            throw new IllegalArgumentException("Airline is not registered for notifications");
        }
        synchronized (notificationsLock) {
            notificationRepository.sendNotification(notificationData);
        }
    }

    @Override
    public BlockingQueue<NotificationData> getNotificationHistory(Airline airline) {
        if (!notificationRepository.isRegisteredForNotifications(airline)) {
            throw new IllegalArgumentException("Airline is not registered for notifications");
        }
        return notificationRepository.getNotificationQueue(airline);
    }
}
