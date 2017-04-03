package com.ysoft.dctrl.event;

/**
 * Created by pilar on 20.3.2017.
 */
public class Event {
    private final String type;
    private final Object data;

    public Event(String type) {
        this(type, null);
    }

    public Event(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
