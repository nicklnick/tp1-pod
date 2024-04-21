package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.AirportRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.PassengerRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;
import ar.edu.itba.pod.grpc.repository.interfaces.PassengerRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.Map;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    private final AirportRepository airportRepository = AirportRepositoryImpl.getInstance();
    private final PassengerRepository passengerRepository = PassengerRepositoryImpl.getInstance();

    @Override
    public void addSector(StringValue request, StreamObserver<Empty> responseObserver) {
        String sectorName = request.getValue();

        try {
            airportRepository.addSector(sectorName);
            Empty grpcResponse = Empty.newBuilder().build();
            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addCounters(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {
        String sectorName = request.getSectorName();
        int count = request.getCounterQty();

        try {
            ContiguousRange range = airportRepository.addCountersToSector(sectorName, count);
            Range grpcRange = Range.newBuilder().setFrom(range.getStart()).setFrom(range.getEnd()).build();
            CounterResponse grpcResponse = CounterResponse.newBuilder().setCounterRange(grpcRange).build();
            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(e);
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
