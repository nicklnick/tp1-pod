package ar.edu.itba.pod.grpc.strategies;

import ar.edu.itba.pod.grpc.counter.CounterMsg;
import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.ContiguousRange;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.Range;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AssignedRangeStrategyImpl implements CounterMsgBuildStrategy {
    @Override
    public CounterMsg buildCounterMsg(Range range, CounterMsg.Builder builder) {
        AssignedRange assignedRange = (AssignedRange) range;
        return builder
                .addAllFlights(assignedRange.getFlights().stream().map(Flight::getCode).collect(Collectors.toCollection(ArrayList::new)))
                .setAirline(assignedRange.getAirline().getName())
                .setPeopleInQueue(assignedRange.getQueueSize())
                .setCounterRange(ar.edu.itba.pod.grpc.commons.Range.newBuilder()
                        .setTo(assignedRange.getEnd())
                        .setFrom(assignedRange.getStart())
                        .build())
                .build();
    }
}
