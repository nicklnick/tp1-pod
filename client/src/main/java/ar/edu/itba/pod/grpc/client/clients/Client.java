package ar.edu.itba.pod.grpc.client.clients;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class Client implements Closeable {
    private final ManagedChannel channel;

    private final Action action;

    public Client() {
        if(!hasValidArguments())
            throw new IllegalArgumentException();

        this.action = getActionClass();
        if(!action.hasValidArguments())
            throw new IllegalArgumentException();

        this.channel = ManagedChannelBuilder.forTarget(System.getProperty(Arguments.SERVER_ADDRESS))
                .usePlaintext()
                .build();
    }

    public abstract Action getActionClass();

    public void run() throws InterruptedException {
        action.execute(channel);
    }

    @Override
    public void close() throws IOException {
        try {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    private boolean hasValidArguments() {
        return System.getProperty(Arguments.SERVER_ADDRESS) != null
                && System.getProperty(Arguments.ACTION) != null;
    }
}
