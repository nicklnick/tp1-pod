package ar.edu.itba.pod.grpc.client.actions.notifications;

import ar.edu.itba.pod.grpc.client.actions.Action;
import ar.edu.itba.pod.grpc.client.constants.Arguments;

import java.util.List;

public enum NotificationsActions {

    REGISTER("register", new RegisterAction(List.of(Arguments.AIRLINE))),
    UNREGISTER("unregister", new UnregisterAction(List.of(Arguments.AIRLINE))),
    HISTORY("history", new HistoryAction(List.of(Arguments.AIRLINE)));

    private final String actionName;
    private final Action action;

    NotificationsActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static NotificationsActions getAction(String actionName) {
        for (NotificationsActions action : NotificationsActions.values()) {
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
