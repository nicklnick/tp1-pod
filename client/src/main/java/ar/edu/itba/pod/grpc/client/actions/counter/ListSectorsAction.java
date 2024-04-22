package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.counter.SectorMsg;
import ar.edu.itba.pod.grpc.counter.SectorResponse;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListSectorsAction extends Action {
    private static final String USAGE_MESSAGE = """
            Usage:
                $> sh client.sh\s
                -DserverAddress=xx.xx.xx.xx:yyyy\s
                -Daction=listSectors
            """;

    public ListSectorsAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {
        final CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);

        try {
            final SectorResponse response = stub.listSectors(Empty.newBuilder().build());
            final List<SectorMsg> sectors = new ArrayList<>(response.getSectorsList());
            sectors.sort(Comparator.comparing(SectorMsg::getName));

            System.out.print(buildResponseHeader());
            sectors.forEach(sector -> System.out.println(buildResponseEntry(sector)));
        } catch (StatusRuntimeException e) {
            throw new IllegalStateException("No sectors to list");
        }
        finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }

    }

    @Override
    public String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    private String buildResponseHeader() {
        return """
                Sectors\tCounters
                ###################
                """;
    }

    private String buildResponseEntry(SectorMsg sector) {
        return String.format("%s\t%s", sector.getName(), getCounterRanges(sector.getCounterRangesList()));
    }

    private String getCounterRanges(List<Range> counterRanges) {
        if(counterRanges.isEmpty())
            return "-";

        final StringBuilder sb = new StringBuilder();
        for(Range counterRange : counterRanges)
            sb.append(String.format("(%d-%d)", counterRange.getFrom(), counterRange.getTo()));

        return sb.toString();
    }
}
