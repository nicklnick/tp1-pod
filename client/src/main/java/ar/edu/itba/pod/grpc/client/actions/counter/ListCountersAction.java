package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.CounterMsg;
import ar.edu.itba.pod.grpc.counter.CounterRequest;
import ar.edu.itba.pod.grpc.counter.CounterResponse;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListCountersAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=listCounters
                -Dsector=sectorName
                -DcounterFrom=counterFrom -DcounterTo=counterTo
            """;

    public ListCountersAction(List<String> arguments) {
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
            final Range range = Range.newBuilder()
                    .setFrom(Integer.parseInt(System.getProperty(Arguments.COUNTER_FROM)))
                    .setTo(Integer.parseInt(System.getProperty(Arguments.COUNTER_TO)))
                    .build();
            final CounterRequest request = CounterRequest.newBuilder()
                    .setSectorName(System.getProperty(Arguments.SECTOR))
                    .setCounterRange(range)
                    .build();

            final CounterResponse response = stub.listCounters(request);
            response.getCountersList().sort(Comparator.comparingInt(c -> c.getCounterRange().getFrom()));

            System.out.print(buildResponseHeader());
            response.getCountersList().forEach(counter -> System.out.println(buildResponseEntry(counter)));
        } catch (StatusRuntimeException e) {
            throw new IllegalStateException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildResponseHeader() {
        return """
                Counters\tAirline\tFlights\tPeople
                #######################################
                """;
    }

    private String buildResponseEntry(CounterMsg counter) {
        return String.format("(%d-%d)\t%s\t%s\t%d",
                counter.getCounterRange().getFrom(),
                counter.getCounterRange().getTo(),
                counter.getAirline(),
                String.join("|", counter.getFlightsList()),
                counter.getPeopleInQueue());
    }
}
