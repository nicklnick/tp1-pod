package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.SectorRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class SectorServiceImpl implements SectorService {
    private static final SectorRepository sectorRepo = SectorRepositoryImpl.getInstance();

    @Override
    public void addSector(String name) {
        final Sector sector = new Sector(name);
        if (containsSector(sector))
            throw new IllegalArgumentException("Sector already exists");

        sectorRepo.addSector(sector);
    }

    @Override
    public Map<Sector, List<Counter>> listSectors() {
        final Map<Sector, List<Counter>> result = sectorRepo.listSectors();
        if(result.isEmpty())
            throw new IllegalStateException("No sectors found");

        return result;
    }

    @Override
    public ContiguousRange addCountersToSector(Sector sector, int count) {
        if (!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if (count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

        return sectorRepo.addCountersToSector(sector, count);
    }

    @Override
    public Map<Sector, List<ContiguousRange>> getContiguousRanges() {
        final Map<Sector, List<ContiguousRange>> result = sectorRepo.getContiguousRanges();
        if(result.isEmpty())
            throw new IllegalStateException("No sectors found");

        return result;
    }

    @Override
    public Map<Sector, List<AssignedRange>> getOnGoingAirlineRange() {
        return sectorRepo.getOnGoingAirlineRange();
    }

    @Override
    public Queue<AssignedRange> getPendingAirlineRange(Sector sector) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        return sectorRepo.getPendingAirlineRange(sector);
    }

    @Override
    public void freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(rangeId <= 0)
            throw new IllegalArgumentException("RangeId must be greater than 0");

        sectorRepo.freeAssignedRange(sector, airline, rangeId);
    }

    @Override
    public void assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

        sectorRepo.assignCounterRangeToAirline(sector, airline, flights, count);
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        return sectorRepo.searchAssignedRangeForAirline(assignedRanges, rangeId, airline);
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters(Optional<Sector> sector) {
        if (sector.isEmpty())
            return sectorRepo.listCounters();

        return sectorRepo.listCounters(sector.get());
    }

    @Override
    public boolean containsSector(Sector sector) {
        return sectorRepo.containsSector(sector);
    }
}
