package com.ysoft.dctrl.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Created by pilar on 20.3.2017.
 */

@Service
public class DefaultEventBus implements EventBus{
    private static final String SEPARATOR = "<<>>";
    private Map<String, Map<Integer, EventHandler>> handlerMap;
    private Integer handlerCounter;

    public DefaultEventBus() {
        handlerMap = new HashMap<>();
        handlerCounter = 0;
    }

    public void publish(Event e) {
        Map<Integer, EventHandler> handlers = handlerMap.get(e.getType());
        if(handlers  == null) { return; }
        handlers.forEach((d, h) -> {
            h.accept(e);
        });
    }

    public String subscribe(String type, EventHandler handler) {
        Map<Integer, EventHandler> handlers = handlerMap.get(type);
        if(handlers == null) {
            handlers = new HashMap<>();
            handlerMap.put(type, handlers);
        }

        handlers.put(handlerCounter, handler);
        return type + SEPARATOR + String.valueOf(handlerCounter++);
    }

    public void unsubscribe(String handlerDescriptor) {
        String[] parts = handlerDescriptor.split(SEPARATOR);
        if(parts.length != 2) { return; }
        Map<Integer, EventHandler> handlers = handlerMap.get(parts[0]);
        if(handlers == null) { return; }
        handlers.remove(Integer.valueOf(parts[1]));

        if(handlers.isEmpty()) {
            handlerMap.remove(parts[0]);
        }
    }
}
