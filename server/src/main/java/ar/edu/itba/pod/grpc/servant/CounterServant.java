package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.counter.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;


public class CounterServant extends CounterServiceGrpc.CounterServiceImplBase {

    @Override
    public void listSectors(Empty request, StreamObserver<SectorResponse> responseObserver) {

    }

    @Override
    public void listCounters(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {


    }

    @Override
    public void assignCounters(AssignRequest request, StreamObserver<AssignResponse> responseObserver) {

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
