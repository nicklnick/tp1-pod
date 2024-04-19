package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.*;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.models.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterServant extends CounterServiceGrpc.CounterServiceImplBase {

    private final Airport airport = Airport.getInstance();

    @Override
    public void listSectors(Empty request, StreamObserver<SectorResponse> responseObserver) {
        try {
            Map<Sector, List<Counter>> response = airport.getCountersBySector();
            List< ar.edu.itba.pod.grpc.counter.Sector> sectors = new ArrayList<>();
            response.forEach((key, counters) -> {
                List<Range> ranges = new ArrayList<>();
                int min = counters.get(0).getNumber();
                int max = counters.get(0).getNumber();
                for (Counter counter : counters) {
                    int aux = counter.getNumber();
                    if (aux > max + 1) {
                        ranges.add(Range.newBuilder().setFrom(min).setTo(max).build());
                        min = aux;
                        max = aux;
                        continue;
                    }
                    max = counter.getNumber();
                }
                ranges.add(Range.newBuilder().setFrom(min).setTo(max).build());
                sectors.add(ar.edu.itba.pod.grpc.counter.Sector.newBuilder().addAllCounterRanges(ranges).setName(key.getName()).build());
            });
            responseObserver.onNext(SectorResponse.newBuilder().addAllSectors(sectors).build());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void listCounters(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {
        try{
            List<Counter> counters = airport.getCountersBySector(new Sector(request.getSectorName()));
            ar.edu.itba.pod.grpc.counter.CounterResponse.Builder countersBuilder = ar.edu.itba.pod.grpc.counter.CounterResponse.newBuilder();
            AtomicInteger num = new AtomicInteger(0);
            counters.forEach(c->{
                Range range = Range.newBuilder().setFrom(c.getCounterRange().getStart()).setFrom(c.getCounterRange().getEnd()).build();
                CounterRange counterRange = c.getCounterRange();
                ar.edu.itba.pod.grpc.counter.Counter.Builder builder = ar.edu.itba.pod.grpc.counter.Counter.newBuilder()
                        .setPeopleInQueue(c.getNumber())
                        .setAirline(counterRange.getFlights().get(0).getAirline().getName())
                        .setCounterRange(range);
                int i = 0;
                List<Flight> flights = counterRange.getFlights();
                for(Flight flight:flights){
                    builder.setFlights(i,flight.getCode());
                    i++;
                }
                countersBuilder.setCounters(num.get(),builder.build());
                num.incrementAndGet();
            });

        }catch (IllegalArgumentException e){
            responseObserver.onError(e);
        }

    }

    @Override
    public void assignCounters(AssignRequest request, StreamObserver<AssignResponse> responseObserver) {
        try {
            CounterRange range = airport.assignCounters(request.getAirline(), request.getFlightsList(), request.getSectorName(), request.getCounterQty());

            responseObserver.onNext(AssignResponse.newBuilder().setCounterRange(Range.newBuilder().setFrom(range.getStart()).setTo(range.getEnd()).build()).setStatus(AssignmentStatus.CONFIRMED).build());

        } catch (IllegalArgumentException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void freeCounters(FreeCountersRequest request, StreamObserver<RepeatedFreeCountersResponse> responseObserver) {

    }

    @Override
    public void checkInCounters(CheckInRequest request, StreamObserver<CheckInResponse> responseObserver) {
        super.checkInCounters(request, responseObserver);
    }

    @Override
    public void listPendingAssignments(StringValue request, StreamObserver<RepeatedFreeCountersResponse> responseObserver) {
        super.listPendingAssignments(request, responseObserver);
    }
}
