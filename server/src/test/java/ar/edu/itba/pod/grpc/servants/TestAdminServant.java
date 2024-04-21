package ar.edu.itba.pod.grpc.servants;

import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.AirportRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.AirportRepository;
import ar.edu.itba.pod.grpc.servant.AdminServant;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class TestAdminServant {

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void addSector() {
        AirportRepository airport = AirportRepositoryImpl.getInstance();
        String sectorNameA = "A";
        String sectorNameB = "B";

        airport.addSector(sectorNameA);
        airport.addSector(sectorNameB);

        System.out.println(airport.getSectors().keySet());
    }

    @Test
    public void addCounters() {
        AirportRepository airport = AirportRepositoryImpl.getInstance();
        String sectorNameA = "A";
        String sectorNameB = "B";
        int count = 5;

        airport.addSector(sectorNameA);
        airport.addSector(sectorNameB);
        airport.addCountersToSector(sectorNameA, count);
        airport.addCountersToSector(sectorNameB, count);

        System.out.println(airport.getContiguousRanges().toString());
    }

}
