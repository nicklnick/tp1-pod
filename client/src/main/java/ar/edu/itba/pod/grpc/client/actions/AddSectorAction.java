package ar.edu.itba.pod.grpc.client.actions;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddSectorAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=addSector
                -Dsector=sectorName
            """;

    public AddSectorAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        try {
            final String sectorName = System.getProperty(Arguments.SECTOR);
            final StringValue request = StringValue.newBuilder().setValue(sectorName).build();

            final Empty ignored = stub.addSector(request);

            System.out.println(buildOuputMessage(sectorName));
        } catch (Exception e) {
            // TODO: Handle error and make status user exception
            System.out.println(e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Override
    public String buildOuputMessage(String... arguments) {
        return String.format("Sector %s added successfully", arguments[0]);
    }
}
