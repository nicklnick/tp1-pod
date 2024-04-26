package ar.edu.itba.pod.grpc.client.actions.checkin;

import ar.edu.itba.pod.grpc.checkin.CheckInServiceGrpc;
import ar.edu.itba.pod.grpc.checkin.PassengerCheckInStatus;
import ar.edu.itba.pod.grpc.checkin.PassengerStatusResponse;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PassengerStatusAction extends Action {
    private static final String USAGE_MESSSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=fetchCounter
                -Dbooking=bookingId
            """;
    public PassengerStatusAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && System.getProperty(Arguments.BOOKING) != null;
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final CheckInServiceGrpc.CheckInServiceBlockingStub stub = CheckInServiceGrpc.newBlockingStub(channel);

        try {
            final String bookingId = System.getProperty(Arguments.BOOKING);
            final StringValue request = StringValue.newBuilder().setValue(bookingId).build();

            final PassengerStatusResponse response = stub.passengerStatus(request);

            System.out.println(buildOutputMessage(response, bookingId));
        } catch (IllegalArgumentException e) {
            throw new InterruptedException(USAGE_MESSSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildOutputMessage(PassengerStatusResponse response, String bookingId) {
        if(response.getStatus() == PassengerCheckInStatus.PENDING_CHECKIN) {
            return String.format(
                    "Booking %s for flight %s from %s can check-in on\n" +
                            " counters (%d-%d) in Sector %s",
                    bookingId, response.getFlight(), response.getAirline(), response.getCounterRange().getFrom(), response.getCounterRange().getTo(), response.getSector()
            );
        } else if (response.getStatus() == PassengerCheckInStatus.ONGOING_CHECKIN) {
            return String.format(
                    "Booking %s for flight %s from %s is now waiting\n" +
                            " to check-in on counters (%d-%d) in Sector %s with %d people in line",
                    bookingId, response.getFlight(), response.getAirline(), response.getCounterRange().getFrom(), response.getCounterRange().getTo(), response.getSector(), response.getPeople()
            );
        } else if (response.getStatus() == PassengerCheckInStatus.FINISHED_CHECKIN) {
            return String.format(
                    "Booking %s for flight %s from %s checked in at\n" +
                            " counter %d in Sector %s",
                    bookingId, response.getFlight(), response.getAirline(), response.getCounter(), response.getSector());
        }
        throw new IllegalArgumentException("Unexpected error occurred");
    }
}
