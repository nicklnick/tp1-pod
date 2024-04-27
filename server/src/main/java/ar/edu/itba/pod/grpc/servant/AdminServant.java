package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.services.PassengerServiceImpl;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.PassengerService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    private final SectorService sectorService = new SectorServiceImpl();
    private final PassengerService passengerService = new PassengerServiceImpl();

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
    public void addExpectedPassenger(PassengerRequest request, StreamObserver<PassengerResponse> responseObserver) {
        final Booking booking = new Booking(request.getBooking());
        final Airline airline = new Airline(request.getAirline());
        final Flight flight = new Flight(airline, request.getFlight());

        try {
            passengerService.addExpectedPassenger(booking, flight);
            responseObserver.onNext(PassengerResponse.newBuilder().setSuccess(true).build());
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(PassengerResponse.newBuilder().setSuccess(false).build());
        } finally {
            responseObserver.onCompleted();
        }
    }
}
