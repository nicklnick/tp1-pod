package ar.edu.itba.pod.grpc.client.actions.notifications.messages;

import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.utils.messages.MessageFormatterHandler;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.notifications.NotificationType;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;

import java.util.List;

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
                        formatCountersCount(n.getCounterRangeList()),
                        formatCounterRange(n.getCounterRangeList()),
                        n.getSector(),
                        System.getProperty(Arguments.AIRLINE),
                        formatFlightsList(n.getFlightsList())
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_PASSENGER_STARTED_CHECKIN,
                n -> String.format("Booking %s for flight %s from %s is now waiting to check-in on counters %s in Sector %s with %s people in line",
                        n.getBooking(),
                        formatFlightsList(n.getFlightsList()),
                        System.getProperty(Arguments.AIRLINE),
                        formatCounterRange(n.getCounterRangeList()),
                        n.getSector(),
                        n.getPeople()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_PASSENGER_COMPLETED_CHECKIN,
                n -> String.format("Check-in successful of %s for flight %s at counter %s in Sector %s",
                        n.getBooking(),
                        formatFlightsList(n.getFlightsList()),
                        formatCounterRange(n.getCounterRangeList()),
                        n.getSector()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_DISMISSED_COUNTERS,
                n -> String.format("Ended check-in for flights %s on counters %s from Sector %s",
                        formatFlightsList(n.getFlightsList()),
                        formatCounterRange(n.getCounterRangeList()),
                        n.getSector()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING,
                n -> String.format("%s counters in Sector %s for flights %s is pending with %s other pendings ahead",
                        formatCountersCount(n.getCounterRangeList()),
                        n.getSector(),
                        formatFlightsList(n.getFlightsList()),
                        n.getPendingsAhead()
                )
        );

        this.formatters.registerFormatter(NotificationType.NOTIFICATION_ASSIGNED_COUNTERS_PENDING_CHANGED,
                n -> String.format("%s counters in Sector %s for flights %s is pending with %s other pendings ahead",
                        formatCountersCount(n.getCounterRangeList()),
                        n.getSector(),
                        formatFlightsList(n.getFlightsList()),
                        n.getPendingsAhead()
                )
        );

    }

    public String format(NotificationsResponse notification) {
        return formatters.getFormatter(notification.getType()).format(notification);
    }

    private String formatCounterRange(List<Range> ranges) {
        StringBuilder builder = new StringBuilder();
        for (Range range : ranges) {
            int from = range.getFrom();
            int to = range.getTo();

            if (from == to) {
                builder.append(String.format("%s", from));
            } else
                builder.append(String.format("(%s-%s)", from, to));
        }

        return builder.toString();
    }

    private String formatCountersCount(List<Range> ranges) {
        if (ranges.size() > 1) return "";

        Range range = ranges.get(0);
        int from = range.getFrom();
        int to = range.getTo();

        return String.format("%s", to - from + 1);
    }

    private String formatFlightsList(List<String> strings){
        return String.join("|", strings);
    }


}
