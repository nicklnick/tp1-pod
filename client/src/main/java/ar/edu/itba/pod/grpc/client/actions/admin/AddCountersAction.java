package ar.edu.itba.pod.grpc.client.actions.admin;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.admin.CounterRequest;
import ar.edu.itba.pod.grpc.admin.CounterResponse;
import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddCountersAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
            $> sh adminClient.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=addCounters
                -Dsector=sectorName
                -Dcounters=counterCount
            \s""";

    public AddCountersAction(List<String> arguments) {
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
            final int counterQty = Integer.parseInt(System.getProperty(Arguments.COUNTERS));

            final CounterRequest request = CounterRequest.newBuilder()
                    .setSectorName(sectorName)
                    .setCounterQty(counterQty)
                    .build();

            final CounterResponse response = stub.addCounters(request);

            System.out.println(buildOuputMessage(
                    counterQty,
                    response.getCounterRange().getFrom(),
                    response.getCounterRange().getTo(),
                    sectorName)
            );
        } catch (StatusRuntimeException e) {
            throw new IllegalArgumentException(USAGE_MESSAGE);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }

    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments()
                && System.getProperty(Arguments.SECTOR) != null
                && System.getProperty(Arguments.COUNTERS) != null;
    }

    private String buildOuputMessage(int countersAdded, int from, int to, String sectorName) {
        return String.format(
                "%d new counters (%d-%d) in Sector %s added successfully",
                countersAdded,
                from,
                to,
                sectorName
        );
    }
}
