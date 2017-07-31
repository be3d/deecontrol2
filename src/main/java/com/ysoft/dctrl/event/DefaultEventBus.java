package com.ysoft.dctrl.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Created by pilar on 20.3.2017.
 */

@Service
public class DefaultEventBus implements EventBus{
    private static final String SEPARATOR = "<<>>";

    private static final String ONCE_PREFIX = "ONCE";
    private static final String DEFAULT_PREFIX = "DEFAULT";

    private Map<String, Map<Integer, EventHandler>> handlerMap;
    private Map<String, Map<Integer, EventHandler>> onceHandlerMap;
    private Integer handlerCounter;

    public DefaultEventBus() {
        handlerMap = new ConcurrentHashMap<>();
        onceHandlerMap = new ConcurrentHashMap<>();
        handlerCounter = 0;
    }

    public void publish(Event e) {
        Map<Integer, EventHandler> handlers = handlerMap.get(e.getType());
        if(handlers != null) {
            handlers.forEach((d, h) -> {
                h.accept(e);
            });
        }

        handlers = onceHandlerMap.get(e.getType());
        if(handlers != null) {
            handlers.forEach((d, h) -> {
                h.accept(e);
            });
            onceHandlerMap.remove(e.getType());
        }
    }

    public String subscribe(String type, EventHandler handler) {
        Map<Integer, EventHandler> handlers = handlerMap.get(type);
        if(handlers == null) {
            handlers = new ConcurrentHashMap<>();
            handlerMap.put(type, handlers);
        }

        handlers.put(handlerCounter, handler);
        return DEFAULT_PREFIX + SEPARATOR + type + SEPARATOR + String.valueOf(handlerCounter++);
    }

    public String subscribeOnce(String type, EventHandler handler) {
        Map<Integer, EventHandler> handlers = onceHandlerMap.get(type);
        if(handlers == null) {
            handlers = new ConcurrentHashMap<>();
            onceHandlerMap.put(type, handlers);
        }

        handlers.put(handlerCounter, handler);
        return ONCE_PREFIX + SEPARATOR + type + SEPARATOR + String.valueOf(handlerCounter++);
    }

    public void unsubscribe(String handlerDescriptor) {
        String[] parts = handlerDescriptor.split(SEPARATOR);
        if(parts.length != 3) { return; }
        switch (parts[0]) {
            case DEFAULT_PREFIX:
                remove(handlerMap, parts[1], Integer.valueOf(parts[2]));
                break;
            case ONCE_PREFIX:
                remove(onceHandlerMap, parts[1], Integer.valueOf(parts[2]));
                break;
        }
    }

    private void remove(Map<String, Map<Integer, EventHandler>> map, String type, int handlerID) {
        Map<Integer, EventHandler> handlers = map.get(type);
        if(handlers == null) { return; }
        handlers.remove(handlerID);

        if(handlers.isEmpty()) {
            map.remove(type);
        }
    }
}
