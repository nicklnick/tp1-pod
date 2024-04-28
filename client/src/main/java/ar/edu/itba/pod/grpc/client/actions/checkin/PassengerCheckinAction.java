package ar.edu.itba.pod.grpc.client.actions.checkin;

import ar.edu.itba.pod.grpc.checkin.CheckInServiceGrpc;
import ar.edu.itba.pod.grpc.checkin.PassengerCheckInRequest;
import ar.edu.itba.pod.grpc.checkin.PassengerCheckinResponse;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PassengerCheckinAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=fetchCounter
                -Dbooking=bookingId
                -Dsector=sectorName
                -Dcounter=counterId
            """
            ;

    public PassengerCheckinAction(List<String> arguments) {
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
            final String booking = System.getProperty(Arguments.BOOKING);
            final String sector = System.getProperty(Arguments.SECTOR);
            final int counter = Integer.parseInt(System.getProperty(Arguments.COUNTER));
            final PassengerCheckInRequest request = PassengerCheckInRequest.newBuilder()
                    .setBooking(booking)
                    .setSector(sector)
                    .setCounter(counter)
                    .build();
            final PassengerCheckinResponse response = stub.passengerCheckin(request);

            System.out.println(buildOutputMessage(booking, response.getFlight(), response.getAirline(), response.getCounterRange().getFrom(), response.getCounterRange().getTo(), sector, response.getPeople()));
        } catch (IllegalArgumentException e) {
            throw new InterruptedException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildOutputMessage(String booking, String flight, String airline, int from, int to, String sector, int people) {
        return String.format("Booking %s for flight %s from %s is now waiting\n" +
                " to check-in on counters (%d-%d) in Sector %s with %d people in line", booking, flight, airline, from, to, sector, people);
    }
}
