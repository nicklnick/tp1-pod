package ar.edu.itba.pod.grpc.client;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.admin.PassengerRequest;
import ar.edu.itba.pod.grpc.admin.PassengerResponse;
import ar.edu.itba.pod.grpc.counter.SectorResponse;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("tp1-pod Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

//        try {
//            final AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);
//            final StringValue request = StringValue.newBuilder().setValue(args[0]).build();
//            final Empty response = stub.addSector(request);
//            logger.info(response.toString());
//        } catch (Exception e) {
//            logger.info(e.getMessage());
//        } finally {
//            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
//        }

        try {
            final CompletableFuture<PassengerResponse> passenger = new CompletableFuture<>();
            final AdminServiceGrpc.AdminServiceStub stub = AdminServiceGrpc.newStub(channel);
            final StreamObserver<PassengerResponse> response = new StreamObserver<>() {
                @Override
                public void onNext(PassengerResponse passengerResponse) {
                    if (passengerResponse.getSuccess())
                        logger.info("Booking added successfully");
                    else
                        logger.info("Booking not added");
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                }
            };

            final StreamObserver<PassengerRequest> request = stub.addExpectedPassenger(response);
            request.onNext(PassengerRequest.newBuilder()
                    .setBooking("ABC123")
                    .setFlight("AC987")
                    .setAirline("AirCanada")
                    .build()
            );
            request.onCompleted();
            PassengerResponse passengerResponse = passenger.get();
            logger.info(passengerResponse.toString());
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
