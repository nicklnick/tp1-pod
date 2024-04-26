package ar.edu.itba.pod.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class GrpcClient {

    private final String address;
    private final int port;
    private final long timeout;

    public GrpcClient(String address, int port, long timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    public void init() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Client.class);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();

        try {
            comunicate(channel, logger);
        } finally {
            channel.shutdown().awaitTermination(timeout, TimeUnit.SECONDS);
        }
    }

    protected abstract void comunicate(ManagedChannel channel, Logger logger);




}
