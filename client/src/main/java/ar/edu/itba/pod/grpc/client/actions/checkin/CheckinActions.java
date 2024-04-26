package ar.edu.itba.pod.grpc.client.actions.checkin;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;

import java.util.List;

public enum CheckinActions {
    FETCH_COUNTER("fetchCounter", new FetchCounterAction(List.of(Arguments.BOOKING))),
    PASSENGER_CHECKIN("passengerCheckin", new PassengerCheckinAction(List.of(Arguments.BOOKING, Arguments.SECTOR, Arguments.COUNTER))),
    PASSENGER_STATUS("passengerStatus", new PassengerStatusAction(List.of(Arguments.BOOKING)))
    ;

    private final String actionName;
    private final Action action;

    CheckinActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static CheckinActions getAction(String actionName) {
        for (CheckinActions action : CheckinActions.values()) {
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
