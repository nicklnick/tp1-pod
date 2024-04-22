package ar.edu.itba.pod.grpc.client.clients;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.actions.counter.CounterActions;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.constants.ErrorMessages;

import java.io.IOException;
import java.util.Optional;

public class CounterClient extends Client {
    private static final String USAGE_MSG = """
            Usage:
            $> sh counterClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=actionName\s
                [ -Dsector=sectorName | -DcounterFrom=fromVal\s
                | -DcounterTo=toVal | -Dflights=flights\s
                | -Dairline=airlineName | -DcounterCount=countVal ]
            \s""";

    public static void main(String[] args) throws IOException {
        try (Client client = new CounterClient()) {
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
        return CounterActions.getAction(System.getProperty(Arguments.ACTION)).getAction();
    }
}
