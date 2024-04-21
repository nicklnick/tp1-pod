package ar.edu.itba.pod.grpc.services;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.AssignedRange;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.CheckInRepositoryImpl;
import ar.edu.itba.pod.grpc.repository.interfaces.CheckInRepository;
import ar.edu.itba.pod.grpc.services.interfaces.CheckInService;
import ar.edu.itba.pod.grpc.services.interfaces.SectorService;

import java.util.List;
import java.util.Optional;

public class CheckInServiceImpl implements CheckInService {
    private static final CheckInRepository checkInRepository = CheckInRepositoryImpl.getInstance();

    private final SectorService sectorService = new SectorServiceImpl();

    @Override
    public void counterCheckIn(Sector sector, int rangeId, Airline airline) {
        if(!sectorService.containsSector(sector))
            throw new IllegalArgumentException("Sector does not exist");

        final List<AssignedRange> onGoingAirlineRange = sectorService.getOnGoingAirlineRange().get(sector);
        final Optional<AssignedRange> maybeAssignedRange = sectorService.searchAssignedRangeForAirline(onGoingAirlineRange, rangeId, airline);
        if(maybeAssignedRange.isEmpty())
            throw new IllegalArgumentException("Airline does not have that range assigned");

        final AssignedRange assignedRange = maybeAssignedRange.get();
        if(assignedRange.getQueueSize() == 0)
            return;

        checkInRepository.counterCheckIn(assignedRange);
    }
}
