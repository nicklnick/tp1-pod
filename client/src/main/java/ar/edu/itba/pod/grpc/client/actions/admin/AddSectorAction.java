package ar.edu.itba.pod.grpc.client.actions.admin;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

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
    public void execute(ManagedChannel channel) throws InterruptedException, IllegalArgumentException {
        final AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        try {
            final String sectorName = System.getProperty(Arguments.SECTOR);
            final StringValue request = StringValue.newBuilder().setValue(sectorName).build();

            final Empty ignored = stub.addSector(request);

            System.out.println(buildOuputMessage(sectorName));
        } catch (StatusRuntimeException e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && System.getProperty(Arguments.SECTOR) != null;
    }

    private String buildOuputMessage(String sectorName) {
        return String.format("Sector %s added successfully", sectorName);
    }
}
