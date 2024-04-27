package ar.edu.itba.pod.grpc.client.actions.counter;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;

import java.util.List;

public enum CounterActions {
    LIST_SECTORS("listSectors", new ListSectorsAction(List.of())),
    LIST_COUNTERS("listCounters", new ListCountersAction(List.of(Arguments.SECTOR, Arguments.COUNTER_FROM, Arguments.COUNTER_TO))),
    ASSIGN_COUNTERS("assignCounters", new AssignCountersAction(List.of(Arguments.SECTOR, Arguments.FLIGHTS, Arguments.AIRLINE, Arguments.COUNTER_COUNT))),
    FREE_COUNTERS("freeCounters", new FreeCountersAction(List.of(Arguments.SECTOR, Arguments.COUNTER_FROM, Arguments.AIRLINE))),
    CHECK_IN_COUNTERS("checkinCounters", new CheckInCounters(List.of(Arguments.SECTOR, Arguments.COUNTER_FROM, Arguments.AIRLINE))),
    LIST_PENDING_ASSIGNMENTS("listPendingAssignments", new ListPendingAssignmentsAction(List.of(Arguments.SECTOR)))
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
