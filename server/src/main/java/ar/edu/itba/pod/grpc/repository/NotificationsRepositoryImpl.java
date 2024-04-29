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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NotificationsRepositoryImpl implements NotificationsRepository {
    private static NotificationsRepositoryImpl instance;
    private final Map<Airline, BlockingQueue<NotificationData>> notificationsQueue = new ConcurrentHashMap<>();
    private final Map<Airline, List<NotificationData>> notificationsHistory = new ConcurrentHashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public synchronized static NotificationsRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new NotificationsRepositoryImpl();
        }
        return instance;
    }

    @Override
    public BlockingQueue<NotificationData> registerForNotifications(Airline airline) {
        readWriteLock.writeLock().lock();
        try {
            notificationsHistory.computeIfAbsent(airline, key -> new ArrayList<>());
            return notificationsQueue.computeIfAbsent(airline, key -> new LinkedBlockingQueue<>());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void unregisterForNotifications(Airline airline) {
        readWriteLock.writeLock().lock();
        try {
            notificationsHistory.remove(airline);
            notificationsQueue.remove(airline);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void sendNotification(NotificationData notification) {
        readWriteLock.writeLock().lock();
        try {
            notificationsHistory.computeIfPresent(notification.getAirline(), (key, value) -> { value.add(notification); return value; });
            notificationsQueue.computeIfPresent(notification.getAirline(), (key, value) -> { value.add(notification); return value; });
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isRegisteredForNotifications(Airline airline) {
        readWriteLock.readLock().lock();
        try {
            return notificationsQueue.containsKey(airline);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<NotificationData> getNotificationsHistory(Airline airline) {
        readWriteLock.readLock().lock();
        try {
            return notificationsHistory.getOrDefault(airline, null);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
