package ar.edu.itba.pod.grpc.servant;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.query.*;
import ar.edu.itba.pod.grpc.services.HistoryServiceImpl;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.HistoryService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {

    private final HistoryService historyService = new HistoryServiceImpl();
    private final SectorService sectorService = new SectorServiceImpl();

    @Override
    public void queryCounterStatus(QueryCounterRequest request, StreamObserver<RepeatedQueryCounterResponse> responseObserver) {
        final Optional<Sector> maybeSector = request.hasSector() ? Optional.of(new Sector(request.getSector())) : Optional.empty();

        try {
            final Map<Sector, List<AssignedRange>> sectorCounters = sectorService.listCounters(maybeSector);

            final RepeatedQueryCounterResponse response = RepeatedQueryCounterResponse.newBuilder()
                    .addAllResponses(sectorCounters.entrySet().stream().map(entry -> {
                        final Sector sector = entry.getKey();
                        final List<AssignedRange> counters = entry.getValue();

                        return counters.stream().map(counter -> QueryCounterResponse.newBuilder()
                                .setSector(sector.getName())
                                .setCounters(Range.newBuilder().setFrom(counter.getStart()).setTo(counter.getEnd()).build())
                                .setAirline(counter.getAirline().getName())
                                .addAllFlights(counter.getFlights().stream().map(Flight::getCode).toList())
                                .setPeople(counter.getQueueSize())
                                .build()
                        ).collect(Collectors.toList());
                    }).flatMap(List::stream).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalStateException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
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
