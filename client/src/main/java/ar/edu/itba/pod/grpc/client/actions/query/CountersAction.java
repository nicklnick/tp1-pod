package ar.edu.itba.pod.grpc.client.actions.query;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.query.QueryCounterRequest;
import ar.edu.itba.pod.grpc.query.QueryCounterResponse;
import ar.edu.itba.pod.grpc.query.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.query.RepeatedQueryCounterResponse;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CountersAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=counters
                -Dsector=sectorName
            """;

    public CountersAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && System.getProperty(Arguments.SECTOR) != null;
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
            final QueryCounterRequest request = QueryCounterRequest.newBuilder()
                    .setSector(sector)
                    .build();

            final RepeatedQueryCounterResponse response = stub.queryCounterStatus(request);

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
                Sector\tCounters\tAirline\tFlights\tPeople
                """;
    }

    private String buildResponseEntry(QueryCounterResponse response) {
        return String.format("%s\t%d\t%d\t%s\t%s,%d",
                response.getSector(),
                response.getCounters().getFrom(),
                response.getCounters().getTo(),
                response.getAirline(),
                buildFlights(response.getFlightsList()),
                response.getPeople());
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
