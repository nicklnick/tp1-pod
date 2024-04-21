package ar.edu.itba.pod.grpc.servants;

import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.services.SectorServiceImpl;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class TestAdminServant {
    private final SectorService sectorService = new SectorServiceImpl();
    final String sectorNameA = "A";
    final String sectorNameB = "B";

    // TODO: Si el mvn clean package te falla por este test debe ser porque crea dos veces los sectores, asiq comentarlo
    // sin miedo porque esto es provisorio

    @Before
    public void setUp() {
        sectorService.addSector(sectorNameA);
        sectorService.addSector(sectorNameB);
    }

    @Test
    public void addSector() {
        System.out.println(sectorService.listSectors().keySet());
    }

    @Test
    public void addCounters() {
        final Sector sectorA = new Sector(sectorNameA);
        final Sector sectorB = new Sector(sectorNameB);

        final int count = 5;

        sectorService.addCountersToSector(sectorA, count);
        sectorService.addCountersToSector(sectorB, count);

        System.out.println(sectorService.getContiguousRanges().toString());
    }

}
