package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.Flight;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.List;

public interface SectorService {
    void addSector(String sector);

    Sector findSectorByName(String sector);

    void listRangesForSector();

    void listRangeBetween(Sector sector, int from, int to);

    void assignRangeToAirlineFlights(Sector sector, int count, Airline airline, List<Flight> flights);

    void freeAirlineRange(Sector sector, int from, Airline airline);

    void checkInBySectorAndRange(Sector sector, int from, Airline airline);

    void listPendingAssignments(Sector sector);
}
