package ar.edu.itba.pod.grpc.client.actions;

import ar.edu.itba.pod.grpc.client.constants.Arguments;

import java.util.List;

public enum AdminActions {
    ADD_SECTOR("addSector", new AddSectorAction(List.of(Arguments.SECTOR)))
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
