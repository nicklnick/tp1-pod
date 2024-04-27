package ar.edu.itba.pod.grpc.client.utils.messages;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatterHandler<T, E> {

    private final Map<T, MessageFormatter<E>> formatterMap;

    public MessageFormatterHandler() {
        this.formatterMap = new HashMap<>();
    }

    public void registerFormatter(T type, MessageFormatter<E> formatter) {
        this.formatterMap.put(type, formatter);
    }

    public MessageFormatter<E> getFormatter(T type) {
        return formatterMap.get(type);
    }

    public String formatMessage(T type, E entity) {
        return formatterMap.get(type).format(entity);
    }
}
