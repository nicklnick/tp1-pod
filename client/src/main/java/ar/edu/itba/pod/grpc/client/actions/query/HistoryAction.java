package ar.edu.itba.pod.grpc.client.actions.query;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.query.QueryCounterHistoryRequest;
import ar.edu.itba.pod.grpc.query.QueryCounterResponse;
import ar.edu.itba.pod.grpc.query.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.query.RepeatedQueryCounterResponse;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=history
                -Dsector=sectorName
                -Dcounter=counterId
            """;

    public HistoryAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && System.getProperty(Arguments.SECTOR) != null && System.getProperty(Arguments.COUNTER) != null;
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);

        try {
            final String sector = System.getProperty(Arguments.SECTOR);
            final int counter = Integer.parseInt(System.getProperty(Arguments.COUNTER));

            final QueryCounterHistoryRequest request = QueryCounterHistoryRequest.newBuilder()
                    .setSector(sector)
                    .setCounter(counter)
                    .build();

            final RepeatedQueryCounterResponse response = stub.queryCounterHistory(request);

            System.out.println(buildResponseHeader());
            response.getResponsesList().forEach(counterResponse -> System.out.println(buildResponseEntry(counterResponse)));

        } catch (Exception e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
    private String buildResponseHeader() {
        return """
                Counters\tAirline\tFlights\tCheck-ins
                ###########################################
                """;
    }
    private String buildResponseEntry(QueryCounterResponse counterResponse) {
        return String.format("(%d-%d)\t%s\t%s\t%d",
                counterResponse.getCounters().getFrom(),
                counterResponse.getCounters().getTo(),
                counterResponse.getAirline(),
                buildFlights(counterResponse.getFlightsList()),
                counterResponse.getPeople());
    }
    private String buildFlights(List<String> flights) {
        if(flights.isEmpty())
            return "-";
        final StringBuilder sb = new StringBuilder();
        for(String flight : flights)
            sb.append(flight).append("|");
        return sb.toString();
    }
}
