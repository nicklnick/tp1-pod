package ar.edu.itba.pod.grpc.models;

public enum NotificationType {
    NOTIFICATION_UNSPECIFIED,
    NOTIFICATION_AIRLINE_ADDED,
    NOTIFICATION_ASSIGNED_COUNTERS,
    NOTIFICATION_PASSENGER_STARTED_CHECKIN,
    NOTIFICATION_PASSENGER_COMPLETED_CHECKIN,
    NOTIFICATION_DISMISSED_COUNTERS,
    NOTIFICATION_ASSIGNED_COUNTERS_PENDING,
    NOTIFICATION_ASSIGNED_COUNTERS_PENDING_CHANGED
}
