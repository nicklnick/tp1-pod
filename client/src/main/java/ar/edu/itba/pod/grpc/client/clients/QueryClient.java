package ar.edu.itba.pod.grpc.client.clients;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.actions.query.QueryActions;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.constants.ErrorMessages;

import java.io.IOException;
import java.util.Optional;

public class QueryClient extends Client {
    private static final String USAGE_MSG = """
            Usage:
            $> sh queryClient.sh\s
            -DserverAddress=xx.xx.xx.xx:yyyy\s
            -Daction=actionName\s
            -DoutPath=query.txt\s
            [ -Dsector=sectorName | -Dairline=airlineName |
            -Dcounter=counterVal ]
            \s""";

    public static void main(String[] args) throws IOException {
        try (Client client = new QueryClient()) {
            client.run();
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorMessages.INVALID_ARGUMENTS);
            System.out.println(Optional.ofNullable(e.getMessage()).orElse(USAGE_MSG));
            System.exit(2);
        } catch (InterruptedException e) {
            System.out.println(ErrorMessages.SERVER_ERROR);
            System.exit(1);
        }
    }

    @Override
    public Action getActionClass() {
        return QueryActions.getAction(System.getProperty(Arguments.ACTION)).getAction();
    }
}
