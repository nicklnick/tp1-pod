package ar.edu.itba.pod.admin;

import ar.edu.itba.pod.grpc.client.GrpcClient;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;

public class AdminClient extends GrpcClient {


    public AdminClient(String address, int port, long timeout) {
        super(address, port, timeout);
    }

    @Override
    protected void comunicate(ManagedChannel channel, Logger logger) {

    }
}
