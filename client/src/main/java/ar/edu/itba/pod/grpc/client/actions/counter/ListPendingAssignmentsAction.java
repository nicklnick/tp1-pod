package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.counter.PendingAssignmentResponse;
import ar.edu.itba.pod.grpc.counter.RepeatedPendingAssignmentResponse;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListPendingAssignmentsAction extends Action {
    private final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=listPendingAssignments
                -Dsector=sectorName
            """;

    public ListPendingAssignmentsAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);

        try {
            final String sector = System.getProperty(Arguments.SECTOR);

            final StringValue request = StringValue.newBuilder().setValue(sector).build();

            final RepeatedPendingAssignmentResponse response = stub.listPendingAssignments(request);

            System.out.println(buildResponseHeader());
            response.getPendingAssignmentsList().forEach(r -> System.out.println(buildResponseEntry(r)));
            
        } catch (StatusRuntimeException e) {
            throw new IllegalStateException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildResponseHeader() {
        return """
                Counters\tAirline\tFlights
                ##############################
                """;
    }

    private String buildResponseEntry(PendingAssignmentResponse response) {
        return String.format("%d\t%s\t%s",
                response.getCounterQty(),
                response.getAirline(),
                String.join("|", response.getFlightsList()));
    }
}
