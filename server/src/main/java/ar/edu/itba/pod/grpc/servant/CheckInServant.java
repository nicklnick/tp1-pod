package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.checkin.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.services.CheckInServiceImpl;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.CheckInService;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class CheckInServant extends CheckInServiceGrpc.CheckInServiceImplBase {

    private final PassengerService passengerService = new PassengerServiceImpl();
    private final CheckInService checkInService = new CheckInServiceImpl();
    private final HistoryService historyService = new HistoryServiceImpl();

    @Override
    public void fetchCounter(StringValue request, StreamObserver<FetchCounterResponse> responseObserver) {
        Booking booking = passengerService.getPassengerBooking(request.getValue());
        Flight flight = passengerService.listExpectedPassengers().get(booking);
        // puede ser null si no existe un rango con el vuelo
        AssignedRange assignedRange = checkInService.getAvailableRangeForCheckIn(booking);
        try {
            final FetchCounterResponse response;
            if(assignedRange == null) {
                response = FetchCounterResponse.newBuilder()
                        .setAirline(flight.getAirline().getName())
                        .setFlight(flight.getCode())
                        .build();
            }
            else {
                response = FetchCounterResponse.newBuilder()
                        .setCounterRange(
                                Range.newBuilder()
                                        .setFrom(assignedRange.getStart())
                                        .setTo(assignedRange.getEnd())
                                        .build())
                        .setSector(assignedRange.getSector().getName())
                        .setPeople(assignedRange.getQueueSize())
                        .setAirline(assignedRange.getAirline().getName())
                        .setFlight(flight.getCode())
                        .build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void passengerCheckin(PassengerCheckInRequest request, StreamObserver<PassengerCheckinResponse> responseObserver) {
        Booking booking = new Booking(request.getBooking());
        Sector sector = new Sector(request.getSector());
        Flight flight = passengerService.listExpectedPassengers().get(booking);


        try {
            AssignedRange assignedRange = checkInService.placePassengerInAssignedRangeQueue(booking, sector, request.getCounter());
            final PassengerCheckinResponse response = PassengerCheckinResponse.newBuilder()
                    .setCounterRange(
                            Range.newBuilder()
                                    .setFrom(assignedRange.getStart())
                                    .setTo(assignedRange.getEnd())
                                    .build())
                    .setBooking(request.getBooking())
                    .setAirline(assignedRange.getAirline().getName())
                    .setPeople(assignedRange.getQueueSize())
                    .setFlight(flight.getCode())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void passengerStatus(StringValue request, StreamObserver<PassengerStatusResponse> responseObserver) {
        Booking booking = new Booking(request.getValue());
        Flight flight = passengerService.listExpectedPassengers().get(booking);
        PassengerStatus status = passengerService.listPassengerStatus().get(booking);
        try {
            AssignedRange assignedRange = checkInService.getPassengerCheckInStatus(booking);
            final PassengerStatusResponse.Builder response = PassengerStatusResponse.newBuilder();
            if(assignedRange == null) {
                Optional<Booking> maybeBooking = Optional.of(booking);
                CheckIn checkInData = historyService.getPassengerCheckIn(maybeBooking);
                response
                        .setCounter(checkInData.getCounter().getNumber())
                        .setAirline(checkInData.getAirline().getName())
                        .setFlight(checkInData.getFlight().getCode())
                        .setStatus(PassengerCheckInStatus.valueOf(status.name()))
                        ;
            } else {
                response.setCounterRange(
                        Range.newBuilder()
                                .setFrom(assignedRange.getStart())
                                .setTo(assignedRange.getEnd())
                                .build())
                .setSector(assignedRange.getSector().getName())
                .setAirline(assignedRange.getAirline().getName())
                .setPeople(assignedRange.getQueueSize())
                .setFlight(flight.getCode())
                .setStatus(PassengerCheckInStatus.valueOf(status.name()))
                ;
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }
}
