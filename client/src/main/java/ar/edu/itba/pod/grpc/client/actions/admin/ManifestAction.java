package ar.edu.itba.pod.grpc.client.actions.admin;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.admin.PassengerRequest;
import ar.edu.itba.pod.grpc.admin.PassengerResponse;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import ar.edu.itba.pod.grpc.client.utils.files.CsvFileReader;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ManifestAction extends Action {

    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=manifest
                -DinPath=path/to/file.csv
            """;

    public ManifestAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        final String csvFilePath = System.getProperty(Arguments.IN_PATH);
        CsvFileReader.readRows(csvFilePath, (row) -> {
            final PassengerRequest request = PassengerRequest.newBuilder()
                    .setBooking(row[0])
                    .setFlight(row[1])
                    .setAirline(row[2])
                    .build();

            System.out.println(request);

//            final PassengerResponse response = stub.addExpectedPassenger(request);
//
//            if (response.getSuccess()) {
//                final String message = buildOutputMessage(request);
//                System.out.println(message);
//            }
        });
    }

    private String buildOutputMessage(PassengerRequest request) {
        return String.format("Booking %s for %s %s added successfully",
                request.getBooking(),
                request.getAirline(),
                request.getFlight());
    }
}
