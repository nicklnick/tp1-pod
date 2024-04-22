package ar.edu.itba.pod.grpc.client.actions;

import io.grpc.ManagedChannel;

import java.util.List;

public abstract class Action {
    private final List<String> arguments;

    public Action(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean hasValidArguments() {
        for(String argument : arguments)
            if(System.getProperty(argument) == null)
                return false;

        return true;
    }

    public abstract String getUsageMessage();

    public abstract void execute(ManagedChannel channel) throws InterruptedException;

    public abstract String buildOuputMessage(String ... arguments);
}
