package ar.edu.itba.pod.grpc.server;

import ar.edu.itba.pod.grpc.servant.AdminServant;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        int port = Integer.parseInt(Optional.ofNullable(System.getProperty("port")).orElse("50051"));

        final io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdminServant())
                .build();
        server.start();

        logger.info("Server started, listening on " + port);
        server.awaitTermination();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }
}
