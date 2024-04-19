package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;
import ar.edu.itba.pod.grpc.repository.interfaces.HistoryRepository;

import java.util.Map;

public class HistoryRepositoryImpl implements HistoryRepository {
    Map<Sector, CheckIn> history;
    Map<Airline,CheckIn> history2;
}
