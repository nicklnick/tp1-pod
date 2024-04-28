package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.*;
import ar.edu.itba.pod.grpc.repository.SectorRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.SectorRepository;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import javax.swing.text.html.Option;
import java.util.*;

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
    public Optional<AssignedRange> freeAssignedRange(Sector sector, Airline airline, int rangeId) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(rangeId <= 0)
            throw new IllegalArgumentException("RangeId must be greater than 0");

        return sectorRepo.freeAssignedRange(sector, airline, rangeId);
    }

    @Override
    public Optional<AssignedRange> assignCounterRangeToAirline(Sector sector, Airline airline, List<Flight> flights, int count) {
        if(!containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");
        else if(count <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

        return sectorRepo.assignCounterRangeToAirline(sector, airline, new ArrayList<>(flights), count);
    }

    @Override
    public List<Range> getRangesBySector(Sector sector, int from, int to) {
        if( to < from)
            throw new IllegalArgumentException("To cannot be lower than from");

        List<AssignedRange> assignedRangeList = sectorRepo.getOnGoingAirlineRangeBySector(sector);
        List<ContiguousRange> contiguousRangeList = sectorRepo.getContiguosRangesBySector(sector);

        List<Range> ranges = new ArrayList<>();
        contiguousRangeList.sort(Comparator.comparingInt(Range::getStart));
        assignedRangeList.sort(Comparator.comparingInt(Range::getStart));



        for (ContiguousRange contiguous:contiguousRangeList) {
            int start = contiguous.getStart();
            int end = contiguous.getEnd();

            if((start >= from || end <= to) && start <= to && end >= from) {
                int lastEndSeen = contiguous.getStart();
                for (AssignedRange current:assignedRangeList) {
                    if(current.getEnd() < contiguous.getStart())
                        continue;
                    if(current.getStart() >= contiguous.getEnd())
                        break;

                    if(current.getStart()-1 > lastEndSeen){
                        int inc = lastEndSeen == contiguous.getStart() ? 0 : 1;
                        ranges.add(new ContiguousRange(lastEndSeen+inc,current.getStart()-1,sector));
                    }
                    lastEndSeen = current.getEnd();
                    ranges.add(current);
                }

                if (lastEndSeen == contiguous.getStart())
                    ranges.add(new ContiguousRange(contiguous.getStart(), contiguous.getEnd(),sector));

                else if (lastEndSeen + 1 < contiguous.getEnd())
                    ranges.add(new ContiguousRange(lastEndSeen+1,contiguous.getEnd(),sector));
            }
        }

        if(ranges.isEmpty())
            throw new IllegalArgumentException("The provided range does not contain any counter");

        return ranges;
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirline(List<AssignedRange> assignedRanges, int rangeId, Airline airline) {
        return sectorRepo.searchAssignedRangeForAirline(assignedRanges, rangeId, airline);
    }

    @Override
    public Optional<AssignedRange> searchAssignedRangeForAirlineBySector(int rangeId, Airline airline, Sector sector){
        return sectorRepo.searchAssignedRangeForAirlineBySector(rangeId,airline,sector);
    }

    @Override
    public Map<Sector, List<AssignedRange>> listCounters(Optional<Sector> sector) {
        if(!sectorRepo.airportContainsAtLeastOneCounter())
            throw new IllegalStateException("No counters found");

        if (sector.isEmpty())
            return sectorRepo.listCounters();

        return sectorRepo.listCounters(sector.get());
    }

    @Override
    public boolean containsSector(Sector sector) {
        return sectorRepo.containsSector(sector);
    }

    @Override
    public int getPendingAssignmentsAheadOf(Sector sector, AssignedRange range) {
        return sectorRepo.getPendingAssignmentsAheadOf(sector, range);
    }
}
