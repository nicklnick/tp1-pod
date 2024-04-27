package ar.edu.itba.pod.grpc.client.utils.messages;

import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;

@FunctionalInterface
public interface MessageFormatter<T> {

    String format(T entity);
}