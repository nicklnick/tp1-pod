package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.counter.CheckInRequest;
import ar.edu.itba.pod.grpc.counter.CheckInResponse;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.counter.RepeatedCheckInResponse;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckInCounters extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=checkinCounters
                -Dsector=sectorName
                -DcounterFrom=counterFrom
                -Dairline=airlineName
            """;

    public CheckInCounters(List<String> arguments) {
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

            final CheckInRequest request = CheckInRequest.newBuilder()
                    .setSectorName(sector)
                    .setCounterFrom(counterFrom)
                    .setAirline(airline)
                    .build();

            final RepeatedCheckInResponse response = stub.checkInCounters(request);
            for(CheckInResponse checkInResponse : response.getCheckInResponsesList()) {
                if(checkInResponse.hasBooking() && checkInResponse.hasFlight())
                    System.out.println(buildSuccessfulResponse(checkInResponse));
                else
                    System.out.println(buildIdleResponse(checkInResponse));
            }
        } catch (StatusRuntimeException e) {
            throw new IllegalStateException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildSuccessfulResponse(CheckInResponse response) {
        return String.format("Check-in successful of %s for flight %s at counter %d",
                response.getBooking(),
                response.getFlight(),
                response.getCounter()
        );
    }

    private String buildIdleResponse(CheckInResponse response) {
        return String.format("Counter %d is idle",
                response.getCounter()
        );
    }
}
