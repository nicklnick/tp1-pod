package ar.edu.itba.pod.grpc.client.constants;

public class Arguments {

    private Arguments() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static final String SERVER_ADDRESS = "serverAddress";
    public static final String ACTION = "action";

    public static final String SECTOR = "sector";
    public static final String COUNTERS = "counters";
    public static final String AIRLINE = "airline";

    public static final String BOOKING = "booking";
    public static final String COUNTER = "counter";

}
