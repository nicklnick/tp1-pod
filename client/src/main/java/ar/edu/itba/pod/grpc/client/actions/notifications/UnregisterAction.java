package ar.edu.itba.pod.grpc.client.actions.notifications;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.notifications.NotificationsResponse;
import ar.edu.itba.pod.grpc.notifications.NotificationsServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UnregisterAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh eventsClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=unregister
                -Dairline=airlineName
            """
            ;

    public UnregisterAction(List<String> arguments) {
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

            final Empty ignored = stub.unregisterForNotifications(request);

            final String message = buildOutputMessage(airlineName);
            System.out.println(message);
        } catch (StatusRuntimeException e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildOutputMessage(String airlineName) {
        return String.format("%s unregistered successfully for events", airlineName);
    }
}
