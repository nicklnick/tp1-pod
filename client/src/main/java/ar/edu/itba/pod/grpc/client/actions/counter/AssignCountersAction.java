package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.counter.AssignRequest;
import ar.edu.itba.pod.grpc.counter.AssignResponse;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssignCountersAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=assignCounters
                -Dsector=sectorName
                -Dflights=flight|flight|...
                -Dairline=airlineName
                -DcounterCount=counterCount
            """;

    public AssignCountersAction(List<String> arguments) {
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
            final List<String> flights = List.of(System.getProperty(Arguments.FLIGHTS).split("\\|"));
            final AssignRequest request = AssignRequest.newBuilder()
                    .setSectorName(System.getProperty(Arguments.SECTOR))
                    .setAirline(System.getProperty(Arguments.AIRLINE))
                    .setCounterQty(Integer.parseInt(System.getProperty(Arguments.COUNTER_COUNT)))
                    .addAllFlights(flights)
                    .build();

            final AssignResponse response = stub.assignCounters(request);
            if(response.hasPendingsAhead())
                System.out.println(buildFailedResponse(request, response));
            else
                System.out.println(buildSuccessfulResponse(request, response));

        } catch (StatusRuntimeException e) {
            throw new IllegalStateException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildSuccessfulResponse(AssignRequest request, AssignResponse response) {
        return String.format("%d counters (%d-%d) in Sector %s are now checking in passengers from %s %s",
                request.getCounterQty(),
                response.getCounterRange().getFrom(),
                response.getCounterRange().getTo(),
                request.getSectorName(),
                request.getAirline(),
                String.join("|", request.getFlightsList())
                );
    }

    private String buildFailedResponse(AssignRequest request, AssignResponse response) {
        return String.format("%d counters in Sector %s is pending with %d other pendings ahead",
                request.getCounterQty(),
                request.getSectorName(),
                response.getPendingsAhead()
        );
    }
}
