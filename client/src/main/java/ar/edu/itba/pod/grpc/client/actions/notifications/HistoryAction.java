package ar.edu.itba.pod.grpc.client.actions.notifications;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.actions.notifications.messages.NotificationResponseMessageHandler;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.notifications.NotificationsHistoryResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsServiceGrpc;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh eventsClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=history
                -Dairline=airlineName
            """
            ;

    final NotificationResponseMessageHandler messageHandler = new NotificationResponseMessageHandler();


    public HistoryAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public String getUsageMessage() {
        return null;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final NotificationsServiceGrpc.NotificationsServiceBlockingStub stub = NotificationsServiceGrpc.newBlockingStub(channel);

        try {
            final String airlineName = System.getProperty(Arguments.AIRLINE);
            final StringValue request = StringValue.newBuilder().setValue(airlineName).build();

            final NotificationsHistoryResponse response = stub.notificationHistory(request);

            for (NotificationsResponse notification : response.getNotificationsList()) {
                String message = buildOutputMessage(notification);
                System.out.println(message);
            }
        } catch (StatusRuntimeException e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        }
        finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }

    }

    private String buildOutputMessage(NotificationsResponse notificationsResponse) {
        return messageHandler.format(notificationsResponse);
    }
}
