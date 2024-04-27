package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.*;
import ar.edu.itba.pod.grpc.models.ContiguousRange;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CounterServant extends CounterServiceGrpc.CounterServiceImplBase {
    private final SectorService sectorService = new SectorServiceImpl();

    @Override
    public void listSectors(Empty request, StreamObserver<SectorResponse> responseObserver) {
        final Map<Sector, List<ContiguousRange>> sectors = sectorService.getContiguousRanges();

        try {
            final SectorResponse.Builder responseBuilder = SectorResponse.newBuilder();
            responseBuilder.addAllSectors(sectors.entrySet().stream().map(entry -> {
                final Sector sector = entry.getKey();
                final List<ContiguousRange> counters = entry.getValue();

                final SectorMsg.Builder sectorMsg = SectorMsg.newBuilder();
                sectorMsg.setName(sector.getName());
                sectorMsg.addAllCounterRanges(counters.stream().map(counter -> {
                    final Range.Builder range = Range.newBuilder();
                    range.setFrom(counter.getStart());
                    range.setTo(counter.getEnd());
                    return range.build();
                }).collect(Collectors.toList()));

                return sectorMsg.build();
            }).collect(Collectors.toList()));

            final SectorResponse response = responseBuilder.build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalStateException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void listCounters(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {


    }

    @Override
    public void assignCounters(AssignRequest request, StreamObserver<AssignResponse> responseObserver) {

    }

    @Override
    public void freeCounters(FreeCountersRequest request, StreamObserver<FreeCountersResponse> responseObserver) {


    }

    @Override
    public void checkInCounters(CheckInRequest request, StreamObserver<RepeatedCheckInResponse> responseObserver) {
        super.checkInCounters(request, responseObserver);
    }

    @Override
    public void listPendingAssignments(StringValue request, StreamObserver<RepeatedPendingAssignmentResponse> responseObserver) {
        super.listPendingAssignments(request, responseObserver);
    }
}
