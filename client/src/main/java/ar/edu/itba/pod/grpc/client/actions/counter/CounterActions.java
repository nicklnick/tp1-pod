package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;

import java.util.List;

public enum CounterActions {
    LIST_SECTORS("listSectors", new ListSectorsAction(List.of()))
    ;

    private final String actionName;
    private final Action action;

    CounterActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static CounterActions getAction(String actionName) {
        for (CounterActions action : CounterActions.values()) {
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
