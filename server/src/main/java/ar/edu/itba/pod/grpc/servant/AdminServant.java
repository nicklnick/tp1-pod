package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.grpc.models.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    private final Airport airport = Airport.getInstance();

    @Override
    public void addSector(StringValue request, StreamObserver<Empty> responseObserver) {
        String sectorName = request.getValue();

        Sector sector = new Sector(sectorName);
        try {
            airport.addSector(sector);
            responseObserver.onNext(Empty.newBuilder().build());
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
                Airline airline = new Airline(passengerRequest.getAirline());
                Flight flight = new Flight(airline, passengerRequest.getFlight());
                Booking booking = new Booking(flight, passengerRequest.getBooking());

                Passenger passenger = new Passenger(booking, Status.PENDING);
                try {
                    airport.addExpectedPassenger(passenger);
                    responseObserver.onNext(PassengerResponse.newBuilder().setSuccess(true).build());
                } catch (IllegalArgumentException e) {
                    responseObserver.onNext(PassengerResponse.newBuilder().setSuccess(false).build());
                }
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
