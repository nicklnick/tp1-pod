package ar.edu.itba.pod.grpc.repository.interfaces;

public interface PassengerRepository {

    public void addExpectedPassenger(String booking, String flight, String airline);

}
