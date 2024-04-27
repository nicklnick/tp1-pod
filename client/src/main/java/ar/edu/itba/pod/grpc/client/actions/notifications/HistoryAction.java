package ar.edu.itba.pod.grpc.client.actions.notifications;

import ar.edu.itba.pod.grpc.client.actions.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class HistoryAction extends Action {
    public HistoryAction(List<String> arguments) {
        super(arguments);
    }

    @Override
    public String getUsageMessage() {
        return null;
    }

    @Override
    public void execute(ManagedChannel channel) throws InterruptedException {

    }
}
