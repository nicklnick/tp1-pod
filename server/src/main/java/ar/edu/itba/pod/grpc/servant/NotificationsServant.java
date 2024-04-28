package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.NotificationData;
import ar.edu.itba.pod.grpc.models.NotificationType;
import ar.edu.itba.pod.grpc.notifications.NotificationsHistoryResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsServiceGrpc;
import ar.edu.itba.pod.grpc.services.NotificationsServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.NotificationsService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class NotificationsServant extends NotificationsServiceGrpc.NotificationsServiceImplBase {

    private final NotificationsService notificationsService = new NotificationsServiceImpl();

    @Override
    public void registerForNotifications(StringValue request, StreamObserver<NotificationsResponse> responseObserver) {
        final String airlineName = request.getValue();
        final Airline airline = new Airline(airlineName);
        try {
            BlockingQueue<NotificationData> notificationsQueue = notificationsService.registerForNotifications(airline);
            NotificationData notification = notificationsQueue.take();
            while (notification.getType() != NotificationType.NOTIFICATION_UNREGISTER) {
                NotificationsResponse response = mapNotificationData(notification);
                responseObserver.onNext(response);
                notification = notificationsQueue.take();
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        } catch (InterruptedException e) {
            responseObserver.onError(Status.ABORTED.asRuntimeException());
        }
    }

    @Override
    public void unregisterForNotifications(StringValue request, StreamObserver<Empty> responseObserver) {
        final String airlineName = request.getValue();
        final Airline airline = new Airline(airlineName);

        try {
            notificationsService.unregisterForNotifications(airline);

            final Empty response = Empty.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
    }

    @Override
    public void notificationHistory(StringValue request, StreamObserver<NotificationsHistoryResponse> responseObserver) {
        super.notificationHistory(request, responseObserver);
    }


    private NotificationsResponse mapNotificationData(NotificationData notification) {
        NotificationsResponse.Builder builder = NotificationsResponse.newBuilder();

        builder.setType(ar.edu.itba.pod.grpc.notifications.NotificationType.valueOf(notification.getType().name()))
                .setPeople(notification.getPeople())
                .setPendingsAhead(notification.getPendingsAhead());

        if (notification.getSector() != null)
            builder.setSector(notification.getSector());

        if (notification.getCounterRange() != null)
            builder.addAllCounterRange(notification.getCounterRange().stream()
                .map(counter -> Range.newBuilder()
                        .setFrom(counter.getStart())
                        .setTo(counter.getEnd())
                        .build())
                .collect(Collectors.toList()));

        if(notification.getBooking() != null)
            builder.setBooking(notification.getBooking());

        if (notification.getFlights() != null)
            builder.addAllFlights(notification.getFlights());

        if (notification.getAirline() != null)
            builder.setAirline(notification.getAirline().getName());

        return builder.build();
    }

}
