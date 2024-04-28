package ar.edu.itba.pod.grpc.client.actions.notifications.messages;

import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.utils.messages.MessageFormatterHandler;
import ar.edu.itba.pod.grpc.notifications.NotificationType;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;

public class NotificationResponseMessageHandler {

    private final MessageFormatterHandler<NotificationType, NotificationsResponse> formatters = new MessageFormatterHandler<>();

    public NotificationResponseMessageHandler() {
        this.registerHandlers();
    }

    private void registerHandlers() {
        this.formatters.registerFormatter(NotificationType.NOTIFICATION_REGISTER,
                (n) -> String.format("%s registered successfully for check-in events",
                        System.getProperty(Arguments.AIRLINE)
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS,
                n -> String.format("%s counters %s in Sector %s are now checking in passengers from %s %s flights",
                        n.getCounterRangeCount(),
                        n.getCounterRangeList(),
                        n.getSector(),
                        "airlineName",
                        n.getFlightsList()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_PASSENGER_STARTED_CHECKIN,
                n -> String.format("Booking %s for flight %s from %s is now waiting to check-in on counters %s in Sector %s with %s people in line",
                        n.getBooking(),
                        n.getFlightsList(),
                        "airlineName",
                        n.getCounterRangeList(),
                        n.getSector(),
                        n.getPeople()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_PASSENGER_COMPLETED_CHECKIN,
                n -> String.format("Check-in successful of %s for flight %s at counter %s in Sector %s",
                        n.getBooking(),
                        n.getFlightsList(),
                        n.getCounterRangeList(),
                        n.getSector()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_DISMISSED_COUNTERS,
                n -> String.format("Ended check-in for flights %s on counters %s from Sector %s",
                        n.getFlightsList(),
                        n.getCounterRangeList(),
                        n.getSector()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING,
                n -> String.format("%s counters in Sector %s for flights %s is pending with %s other pendings ahead",
                        n.getCounterRangeCount(),
                        n.getSector(),
                        n.getFlightsList(),
                        n.getPendingsAhead()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING_CHANGED,
                n -> String.format("%s counters in Sector %s for flights %s is pending with %s other pendings ahead",
                        n.getCounterRangeCount(),
                        n.getSector(),
                        n.getFlightsList(),
                        n.getPendingsAhead()
                )
        );

    }

    public String format(NotificationsResponse notification) {
        return formatters.getFormatter(notification.getType()).format(notification);
    }
}
