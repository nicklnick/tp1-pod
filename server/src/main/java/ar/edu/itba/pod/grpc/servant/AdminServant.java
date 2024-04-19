package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.grpc.models.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {


    @Override
    public void addSector(StringValue request, StreamObserver<Empty> responseObserver) {
        String sectorName = request.getValue();

        Sector sector = new Sector(sectorName);
        try {

        } catch (IllegalArgumentException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public StreamObserver<PassengerRequest> addExpectedPassenger(StreamObserver<PassengerResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(PassengerRequest passengerRequest) {

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
