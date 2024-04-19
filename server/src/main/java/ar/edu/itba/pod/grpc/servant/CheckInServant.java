package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.checkin.*;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

public class CheckInServant extends CheckInServiceGrpc.CheckInServiceImplBase {

    @Override
    public void fetchCounter(StringValue request, StreamObserver<FetchCounterResponse> responseObserver) {
        super.fetchCounter(request, responseObserver);
    }

    @Override
    public void passengerCheckin(PassengerCheckInRequest request, StreamObserver<PassengerCheckinResponse> responseObserver) {
        super.passengerCheckin(request, responseObserver);
    }

    @Override
    public void passengerStatus(StringValue request, StreamObserver<PassengerStatusResponse> responseObserver) {
        super.passengerStatus(request, responseObserver);
    }
}
