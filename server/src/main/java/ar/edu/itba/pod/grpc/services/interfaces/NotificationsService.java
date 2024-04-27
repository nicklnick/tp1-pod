package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;

import java.util.concurrent.BlockingQueue;

public interface NotificationsService {
    BlockingQueue<NotificationData> registerForNotifications(Airline airline);
    void unregisterForNotifications(Airline airline);
    void sendNotification(NotificationData notificationData);
    BlockingQueue<NotificationData> getNotificationHistory(Airline airline);
}
