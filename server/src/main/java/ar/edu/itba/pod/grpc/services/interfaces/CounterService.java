package ar.edu.itba.pod.grpc.services.interfaces;

import ar.edu.itba.pod.grpc.models.Sector;

public interface CounterService {
    void addCounter(Sector sector, int count);
}
