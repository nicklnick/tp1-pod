package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Airline;

public interface AirlineService {
    void addAirline(String airline);

    Airline findAirlineByName(String name);
}
