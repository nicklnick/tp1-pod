package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.*;
import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.*;
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
        final Sector sector = new Sector(request.getSectorName());
        final Airline airline = new Airline(request.getAirline());
        final List<Flight> flights = request.getFlightsList().stream().map(f -> new Flight(airline, f)).toList();

        try {
            final Optional<AssignedRange> maybeRange = sectorService.assignCounterRangeToAirline(sector, airline, flights, request.getCounterQty());
            final AssignResponse.Builder response = AssignResponse.newBuilder();
            if(maybeRange.isPresent()) {
                final Range range = Range.newBuilder()
                        .setFrom(maybeRange.get().getStart())
                        .setTo(maybeRange.get().getEnd())
                        .build();

                response.setCounterRange(range)
                        .setStatus(AssignmentStatus.READY_FOR_CHECKING);
            } else {
                final int pendingAssignments =
                        sectorService.getPendingAssignmentsAheadOf(sector, new AssignedRange(sector, airline, request.getCounterQty()));

                response.setStatus(AssignmentStatus.PENDING_ASSIGNATION)
                        .setPendingsAhead(pendingAssignments);
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }
    @Override
    public void freeCounters(FreeCountersRequest request, StreamObserver<FreeCountersResponse> responseObserver) {
        Sector sector = new Sector(request.getSectorName());
        Airline airline = new Airline(request.getAirline());
        int rangedId = request.getCounterFrom();
        try {
            AssignedRange assignedRange = sectorService.freeAssignedRange(sector, airline, rangedId).orElseThrow(Status.NOT_FOUND::asRuntimeException);
            final FreeCountersResponse response = FreeCountersResponse.newBuilder()
                    .addAllFlights(assignedRange.getFlights().stream().map(Flight::getCode).collect(Collectors.toList()))
                    .setCounterQty(assignedRange.getTotalCounters())
                    .setCounterRange(Range.newBuilder().setFrom(assignedRange.getStart()).setTo(assignedRange.getEnd()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void checkInCounters(CheckInRequest request, StreamObserver<RepeatedCheckInResponse> responseObserver) {
        super.checkInCounters(request, responseObserver);
    }

    @Override
    public void listPendingAssignments(StringValue request, StreamObserver<RepeatedPendingAssignmentResponse> responseObserver) {
        Sector sector = new Sector(request.getValue());
        try {
            Queue<AssignedRange> assignedRanges = sectorService.getPendingAirlineRange(sector);
            List<PendingAssignmentResponse> pendingAssignmentResponses = new ArrayList<>();
            for (AssignedRange assignedRange:assignedRanges) {
                pendingAssignmentResponses.add(PendingAssignmentResponse.newBuilder()
                        .setAirline(assignedRange.getAirline().getName())
                        .setCounterQty(assignedRange.getTotalCounters())
                        .addAllFlights(assignedRange.getFlights().stream().map(Flight::getCode).collect(Collectors.toList()))
                        .build());
            }
            RepeatedPendingAssignmentResponse response = RepeatedPendingAssignmentResponse.newBuilder()
                    .addAllPendingAssignments(pendingAssignmentResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }catch (IllegalArgumentException e){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }

    }
}
