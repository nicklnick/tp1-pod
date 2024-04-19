package ar.edu.itba.pod.grpc.repository;

import ar.edu.itba.pod.grpc.models.Airline;
import ar.edu.itba.pod.grpc.models.CheckIn;
import ar.edu.itba.pod.grpc.models.Sector;

import java.util.Map;

public class HistoryRepositoryImpl {
    Map<Sector, CheckIn> history;
    Map<Airline,CheckIn> history2;
}
