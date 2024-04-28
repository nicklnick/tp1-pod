package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;
import ar.edu.itba.pod.grpc.repository.interfaces.NotificationsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NotificationsRepositoryImpl implements NotificationsRepository {
    private static NotificationsRepositoryImpl instance;
    private final Map<Airline, BlockingQueue<NotificationData>> notificationsQueue = new ConcurrentHashMap<>();
    private final Map<Airline, List<NotificationData>> notificationsHistory = new ConcurrentHashMap<>();

    public synchronized static NotificationsRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new NotificationsRepositoryImpl();
        }
        return instance;
    }

    @Override
    public BlockingQueue<NotificationData> registerForNotifications(Airline airline) {
        notificationsHistory.computeIfAbsent(airline, key -> new ArrayList<>());
        return notificationsQueue.computeIfAbsent(airline, key -> new LinkedBlockingQueue<>());
    }

    @Override
    public void unregisterForNotifications(Airline airline) {
        notificationsHistory.remove(airline);
        notificationsQueue.remove(airline);
    }

    @Override
    public void sendNotification(NotificationData notification) {
        notificationsHistory.computeIfPresent(notification.getAirline(), (key, value) -> { value.add(notification); return value; });
        notificationsQueue.computeIfPresent(notification.getAirline(), (key, value) -> { value.add(notification); return value; });
    }

    @Override
    public boolean isRegisteredForNotifications(Airline airline) {
        return notificationsHistory.containsKey(airline);
    }

    @Override
    public List<NotificationData> getNotificationsHistory(Airline airline) {
        return notificationsHistory.getOrDefault(airline, null);
    }
}
