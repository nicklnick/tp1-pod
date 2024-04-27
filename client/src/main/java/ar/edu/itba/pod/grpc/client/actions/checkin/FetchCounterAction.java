package ar.edu.itba.pod.grpc.client.actions.checkin;

import ar.edu.itba.pod.grpc.checkin.CheckInServiceGrpc;
import ar.edu.itba.pod.grpc.checkin.FetchCounterResponse;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FetchCounterAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=fetchCounter
                -Dbooking=bookingId
            """;

    public FetchCounterAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && System.getProperty(Arguments.BOOKING) != null;
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final CheckInServiceGrpc.CheckInServiceBlockingStub stub = CheckInServiceGrpc.newBlockingStub(channel);

        try {
            final String bookingId = System.getProperty(Arguments.BOOKING);

            final StringValue request = StringValue.newBuilder().setValue(bookingId).build();
            final FetchCounterResponse response = stub.fetchCounter(request);

            if(response.hasCounterRange()) {
                System.out.println(
                        buildOutputMessage(response.getFlight(), response.getAirline(), response.getCounterRange().getFrom(), response.getCounterRange().getTo(), response.getSector(), response.getPeople())
                );
            } else {
                System.out.println(
                        buildOutputMessage(response.getFlight(), response.getAirline(), null, null, null, null)
                );
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }

    }

    private String buildOutputMessage(String flightCode, String airlineName, Integer from, Integer to, String sectorName, Integer peopleInLine) {
       if(sectorName == null) {
           return String.format("Flight %s from %s has no counters assigned yet", flightCode, airlineName);
       }
       else {
           return String.format("Flight %s from %s is now checking in at counters\n" +
                   " (%d-%d) in Sector %s with %d people in line", flightCode, airlineName, from, to, sectorName, peopleInLine);
       }
    }
}
