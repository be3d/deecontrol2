package com.ysoft.dctrl.event;

/**
 * Created by pilar on 20.3.2017.
 */
public interface EventBus {
    void publish(Event e);

    String subscribe(String type, EventHandler eventHandler);
    String subscribeOnce(String type, EventHandler eventHandler);
    void unsubscribe(String handlerDescriptor);
}
