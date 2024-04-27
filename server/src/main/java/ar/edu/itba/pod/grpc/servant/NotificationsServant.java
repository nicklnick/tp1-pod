package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.notifications.NotificationsHistoryResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

public class NotificationsServant extends NotificationsServiceGrpc.NotificationsServiceImplBase {

    @Override
    public void registerForNotifications(StringValue request, StreamObserver<NotificationsResponse> responseObserver) {
        super.registerForNotifications(request, responseObserver);
    }

    @Override
    public void unregisterForNotifications(StringValue request, StreamObserver<Empty> responseObserver) {
        super.unregisterForNotifications(request, responseObserver);
    }

    @Override
    public void notificationHistory(StringValue request, StreamObserver<NotificationsHistoryResponse> responseObserver) {
        super.notificationHistory(request, responseObserver);
    }

}
