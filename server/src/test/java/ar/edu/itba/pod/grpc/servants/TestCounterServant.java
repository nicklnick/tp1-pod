package ar.edu.itba.pod.grpc.servants;

import ar.edu.itba.pod.grpc.commons.Range;
import ar.edu.itba.pod.grpc.counter.CounterRequest;
import ar.edu.itba.pod.grpc.counter.CounterResponse;
import ar.edu.itba.pod.grpc.counter.SectorResponse;
import ar.edu.itba.pod.grpc.models.Airport;
import ar.edu.itba.pod.grpc.models.Counter;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.servant.CounterServant;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestCounterServant {

    @BeforeEach



    @Test
    public void testListSectors(){
        CounterServant servant = new CounterServant();

        StreamObserver<SectorResponse> response = new StreamObserver<SectorResponse>() {

            @Override
            public void onNext(SectorResponse sectorResponse) {
                sectorResponse.getSectorsList().forEach(sector -> {
                    System.out.println(sector);
                });
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }
        };
        Airport airport = Airport.getInstance();

        Sector sector1 = new Sector("A");
        Sector sector2 = new Sector("B");

        airport.addSector(sector1);
        airport.addSector(sector2);
        airport.addCountersRange(sector1,  2);
        airport.addCountersRange(sector2,  2);


        Empty request = null     ;
        servant.listSectors(request, response);
    }

    @Test
    public void testListCounters(){
        CounterServant servant = new CounterServant();

        StreamObserver<CounterResponse> response = new StreamObserver<>() {

            @Override
            public void onNext(CounterResponse counterResponse) {
                counterResponse.getCountersList().forEach(System.out::println);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }
        };
        Airport airport = Airport.getInstance();

        Sector sector1 = new Sector("A");
        Sector sector2 = new Sector("B");

        airport.addSector(sector1);
        airport.addSector(sector2);
        airport.addCountersRange(sector1,  2);
        airport.addCountersRange(sector2,  2);


        CounterRequest request = CounterRequest.newBuilder().setCounterRange(Range.newBuilder().setFrom(1).setTo(10).build()).setSectorName("A").build()    ;
        servant.listCounters(request, response);
    }

    @Test
    public void testFreeCounters(){

    }
}
