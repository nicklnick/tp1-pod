package ar.edu.itba.pod.grpc.client.actions.query;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public enum QueryActions {
    COUNTERS("counters", new CountersAction(List.of(Arguments.SECTOR))),
    CHECK_INS("checkins", new CheckInsAction(List.of(Arguments.SECTOR, Arguments.AIRLINE))),
    HISTORY("history", new HistoryAction(List.of(Arguments.SECTOR, Arguments.COUNTER)))
    ;

    private final String actionName;
    private final Action action;

    QueryActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static QueryActions getAction(String actionName) {
        for (QueryActions action : QueryActions.values()) {
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
