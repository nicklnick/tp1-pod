package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;
import ar.edu.itba.pod.grpc.models.NotificationType;
import ar.edu.itba.pod.grpc.repository.NotificationsRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.NotificationsRepository;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;

import java.util.concurrent.BlockingQueue;

public class NotificationsServiceImpl implements NotificationsService {
    private static final NotificationsRepository notificationRepository = NotificationsRepositoryImpl.getInstance();
    private final Object notificationsLock = "notificationsLock";
    public static NotificationsRepository getNotificationRepository() { return notificationRepository; }
    @Override
    public BlockingQueue<NotificationData> registerForNotifications(Airline airline) {
        BlockingQueue<NotificationData> notificationQueue;
        synchronized (notificationsLock) {
            notificationQueue = notificationRepository.registerForNotifications(airline);
            notificationRepository.sendNotification(new NotificationData(NotificationType.NOTIFICATION_AIRLINE_ADDED, airline));
        }
        return notificationQueue;
    }

    @Override
    public void unregisterForNotifications(Airline airline) {
        if (!notificationRepository.isRegisteredForNotifications(airline)) {
            throw new IllegalArgumentException("Airline is not registered for notifications");
        }
        synchronized (notificationsLock) {
            notificationRepository.unregisterForNotifications(airline);
        }
    }

    @Override
    public void sendNotification(NotificationData notificationData) {
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
