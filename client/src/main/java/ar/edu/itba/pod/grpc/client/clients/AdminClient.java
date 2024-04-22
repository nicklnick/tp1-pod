package ar.edu.itba.pod.grpc.client.clients;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.actions.AdminActions;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.constants.ErrorMessages;

import java.io.IOException;

public class AdminClient extends Client {
    private static final String USAGE_MSG = """
            Usage:
            $> sh adminClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=actionName
                [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath=manifestPath ]\s
            \s""";

    public static void main(String[] args) throws IOException {
        try (Client client = new AdminClient()) {
            client.run();
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorMessages.INVALID_ARGUMENTS);
            System.out.println(USAGE_MSG);
            System.exit(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Action getActionClass() {
        return AdminActions.getAction(System.getProperty(Arguments.ACTION)).getAction();
    }
}
