package ar.edu.itba.pod.grpc.client.clients;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.actions.notifications.NotificationsActions;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.constants.ErrorMessages;

import java.io.IOException;
import java.util.Optional;

public class NotificationsClient extends Client {

    private static final String USAGE_MSG = """
            Usage:
            $> sh eventsClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=actionName
                -Dairline=airlineName\s
            \s""";

    public static void main(String[] args) throws IOException {
        try (Client client = new NotificationsClient()) {
            client.run();
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorMessages.INVALID_ARGUMENTS);
            System.out.println(Optional.ofNullable(e.getMessage()).orElse(USAGE_MSG));
            System.exit(2);
        } catch (IllegalStateException e) {
            System.out.println(ErrorMessages.ILLEGAL_STATE);
            System.out.println(e.getMessage());
            System.exit(2);
        } catch (InterruptedException e) {
            System.out.println(ErrorMessages.SERVER_ERROR);
            System.exit(1);
        }
    }

    @Override
    public Action getActionClass() {
        return NotificationsActions.getAction(System.getProperty(Arguments.ACTION)).getAction();
    }
}
