package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface NotificationsService {
    BlockingQueue<NotificationData> registerForNotifications(Airline airline) throws IllegalArgumentException;
    void unregisterForNotifications(Airline airline) throws IllegalArgumentException;
    void sendNotification(NotificationData notificationData);
    List<NotificationData> getNotificationHistory(Airline airline);
}
