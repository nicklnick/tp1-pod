package ar.edu.itba.pod.grpc.repository.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;

import java.util.concurrent.BlockingQueue;

public interface NotificationsRepository {
    BlockingQueue<NotificationData> registerForNotifications(Airline airline);
    void unregisterForNotifications(Airline airline);
    void sendNotification(NotificationData notification);
    boolean isRegisteredForNotifications(Airline airline);
    BlockingQueue<NotificationData> getNotificationQueue(Airline airline);
}
