package ar.edu.itba.pod.grpc.strategies;

import ar.edu.itba.pod.grpc.counter.CounterMsg;
import ar.edu.itba.pod.grpc.models.ContiguousRange;
import ar.edu.itba.pod.grpc.models.Range;

import java.util.ArrayList;

public class ContiguousRangeStrategyImpl implements CounterMsgBuildStrategy{
    @Override
    public CounterMsg buildCounterMsg(Range range, CounterMsg.Builder builder) {
        ContiguousRange contiguousRange = (ContiguousRange) range;
        return builder.addAllFlights(new ArrayList<>())
                .setCounterRange(ar.edu.itba.pod.grpc.commons.Range.newBuilder()
                        .setTo(contiguousRange.getEnd())
                        .setFrom(contiguousRange.getStart())
                        .build())
                .build();
    }
}
