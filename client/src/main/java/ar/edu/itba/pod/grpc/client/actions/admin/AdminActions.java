package ar.edu.itba.pod.grpc.client.actions.admin;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;

import java.util.List;

public enum AdminActions {
    ADD_SECTOR("addSector", new AddSectorAction(List.of(Arguments.SECTOR))),
    ADD_COUNTERS("addCounters", new AddCountersAction(List.of(Arguments.SECTOR, Arguments.COUNTERS))),
    MANIFEST("manifest", new ManifestAction(List.of(Arguments.IN_PATH)))
    ;

    private final String actionName;
    private final Action action;

    AdminActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static AdminActions getAction(String actionName) {
        for (AdminActions action : AdminActions.values()) {
            if (action.actionName.equalsIgnoreCase(actionName)) {
                return action;
            }
        }
        throw new IllegalArgumentException();
    }

    public Action getAction() {
        return action;
    }
}
