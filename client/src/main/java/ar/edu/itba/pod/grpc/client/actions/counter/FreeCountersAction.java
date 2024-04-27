package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.counter.FreeCountersRequest;
import ar.edu.itba.pod.grpc.counter.FreeCountersResponse;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FreeCountersAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=freeCounters
                -Dsector=sectorName
                -DcounterFrom=counterFrom
                -Dairline=airlineName
            """;

    public FreeCountersAction(List<String> arguments) {
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
            final int counterFrom = Integer.parseInt(System.getProperty(Arguments.COUNTER_FROM));
            final String airline = System.getProperty(Arguments.AIRLINE);

            final FreeCountersRequest request = FreeCountersRequest.newBuilder()
                    .setSectorName(sector)
                    .setCounterFrom(counterFrom)
                    .setAirline(airline)
                    .build();

            final FreeCountersResponse response = stub.freeCounters(request);
            System.out.println(buildResponse(request, response));
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildResponse(FreeCountersRequest request, FreeCountersResponse response) {
        return String.format("Ended check-in for flights %s on %d counters (%d-%d) in Sector %s",
                String.join("|", response.getFlightsList()),
                response.getCounterQty(),
                response.getCounterRange().getFrom(),
                response.getCounterRange().getTo(),
                request.getSectorName()
        );
    }
}
