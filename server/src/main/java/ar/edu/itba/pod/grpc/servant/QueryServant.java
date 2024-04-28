package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.query.*;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {

    private final HistoryService historyService = new HistoryServiceImpl();
    private final SectorService sectorService = new SectorServiceImpl();

    @Override
    public void queryCounterStatus(QueryCounterRequest request, StreamObserver<RepeatedQueryCounterResponse> responseObserver) {

    }

    @Override
    public void queryCheckin(QueryCheckinRequest request, StreamObserver<RepeatedQueryCheckinResponse> responseObserver) {

        try {
            final RepeatedQueryCheckinResponse.Builder response = RepeatedQueryCheckinResponse.newBuilder();
            List<CheckIn> checkIns;
            if(request.hasSector()) {
                Sector sector = new Sector(request.getSector());
                if(request.hasAirline()) {
                    Airline airline = new Airline(request.getAirline());
                    checkIns = historyService.getAirlineCheckInHistory(Optional.of(airline)).stream().filter(checkIn -> checkIn.getSector().equals(sector)).toList();
                } else {
                    checkIns = historyService.getSectorCheckInHistory(Optional.of(sector));
                }
            } else if(request.hasAirline()) {
                Airline airline = new Airline(request.getAirline());
                checkIns = historyService.getAirlineCheckInHistory(Optional.of(airline));
            }
            else {
                checkIns = historyService.getAllCheckIns();
            }
            response.addAllResponses(checkIns.stream().map(checkIn ->
                    QueryCheckinResponse.newBuilder()
                            .setSector(checkIn.getSector().getName())
                            .setCounter(checkIn.getCounter().getNumber())
                            .setAirline(checkIn.getAirline().getName())
                            .setFlight(checkIn.getFlight().getCode())
                            .setBooking(checkIn.getBooking().getCode()).build()
            ).toList());
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void queryCounterHistory(QueryCounterHistoryRequest request, StreamObserver<RepeatedQueryCounterResponse> responseObserver) {
        try {
            final RepeatedQueryCounterResponse.Builder response = RepeatedQueryCounterResponse.newBuilder();
            List<AssignedRange> assignedRanges = historyService.getAssignedRangesHistory();
            Counter counter = new Counter(request.getCounter(), null);
            response.addAllResponses(assignedRanges.stream().filter(assignedRange -> assignedRange.getCountersMap().containsKey(counter)).map(assignedRange ->
                    QueryCounterResponse.newBuilder()
                            .setSector(assignedRange.getSector().getName())
                            .setCounters(Range.newBuilder().setFrom(assignedRange.getStart()).setTo(assignedRange.getEnd()).build())
                            .setAirline(assignedRange.getAirline().getName())
                            .addAllFlights(assignedRange.getFlights().stream().map(Flight::getCode).toList())
                            .setPeople(assignedRange.getQueueSize())
                            .build()

            ).toList());
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
        }
    }
}
