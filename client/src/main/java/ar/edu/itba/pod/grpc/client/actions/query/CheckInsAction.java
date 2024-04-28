package ar.edu.itba.pod.grpc.client.actions.query;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.query.QueryCheckinRequest;
import ar.edu.itba.pod.grpc.query.QueryCheckinResponse;
import ar.edu.itba.pod.grpc.query.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.query.RepeatedQueryCheckinResponse;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckInsAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh passengerClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=checkins
                -Dsector=sectorName
                -Dairline=airlineName
            """;



    public CheckInsAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments();
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);

        try {
            final QueryCheckinRequest.Builder request = QueryCheckinRequest.newBuilder();

            if(System.getProperty(Arguments.SECTOR) != null) {
                if(System.getProperty(Arguments.AIRLINE) != null) {
                    request.setAirline(System.getProperty(Arguments.AIRLINE));
                }
                request.setSector(System.getProperty(Arguments.SECTOR));
            } else if(System.getProperty(Arguments.AIRLINE) != null) {
                request.setAirline(System.getProperty(Arguments.AIRLINE));
            }

            final RepeatedQueryCheckinResponse response = stub.queryCheckin(request.build());

            System.out.println(buildResponseHeader());
            response.getResponsesList().forEach(checkinResponse -> System.out.println(buildResponseEntry(checkinResponse)));
        } catch (Exception e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private String buildResponseHeader() {
        return """
                Sector\tCounter\tAirline\tFlight\tBooking
                ####################################################
                """;
    }

    private String buildResponseEntry(QueryCheckinResponse checkinResponse) {
        return String.format("%s\t%d\t%s\t%s\t%s",
                checkinResponse.getSector(),
                checkinResponse.getCounter(),
                checkinResponse.getAirline(),
                checkinResponse.getFlight(),
                checkinResponse.getBooking()
        );
    }
}
