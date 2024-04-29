package ar.edu.itba.pod.grpc.strategies;

import ar.edu.itba.pod.grpc.counter.CounterMsg;
import ar.edu.itba.pod.grpc.models.Range;

public interface CounterMsgBuildStrategy {

    CounterMsg buildCounterMsg(Range range, CounterMsg.Builder builder);
}
