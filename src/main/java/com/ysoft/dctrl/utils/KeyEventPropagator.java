package com.ysoft.dctrl.utils;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Created by pilar on 7.4.2017.
 */

@Component
public class KeyEventPropagator {
    private ArrayList<EventHandler<? super KeyEvent>> handlers;

    public KeyEventPropagator() {
        handlers = new ArrayList<>();
    }

    public void keyPressed(KeyEvent keyEvent) {
        handlers.forEach(h -> h.handle(keyEvent));
    }

    public void onKeyPressed(EventHandler<? super KeyEvent> handler) {
        handlers.add(handler);
    }
}
