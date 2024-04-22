package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.models.ContiguousRange;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    private final SectorService sectorService = new SectorServiceImpl();

    @Override
    public void addSector(StringValue request, StreamObserver<Empty> responseObserver) {
        final String sectorName = request.getValue();

        try {
            sectorService.addSector(sectorName);

            final Empty grpcResponse = Empty.newBuilder().build();
            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.ALREADY_EXISTS.asRuntimeException());
        }
    }

    @Override
    public void addCounters(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {
        final Sector sector = new Sector(request.getSectorName());
        final int count = request.getCounterQty();

        try {
            final ContiguousRange range = sectorService.addCountersToSector(sector, count);

            int from = range.getStart();
            // if we join the ranges, we have to adjust the from value
            if(count != (range.getEnd() - range.getStart() + 1))
                from = range.getStart() + count;
            final Range responseRange = Range.newBuilder().setFrom(from).setTo(range.getEnd()).build();
            final CounterResponse response = CounterResponse.newBuilder().setCounterRange(responseRange).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public StreamObserver<PassengerRequest> addExpectedPassenger(StreamObserver<PassengerResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(PassengerRequest passengerRequest) {
                // todo
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
