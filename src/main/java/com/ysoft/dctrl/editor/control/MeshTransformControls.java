package com.ysoft.dctrl.editor.control;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MeshView;

/**
 * Created by pilar on 5.7.2017.
 */

@Component
public class MeshTransformControls {
    private EventHandler<Event> onActivated;
    private EventBus eventBus;

    private Mode mode;

    public MeshTransformControls(EventBus eventBus) {
        this.eventBus = eventBus;
        this.mode = Mode.MOVE;
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.CONTROL_MOVE_MODEL_CLICK.name(), (e) -> setMode(Mode.MOVE));
        eventBus.subscribe(EventType.CONTROL_SCALE_MODEL_CLICK.name(), (e) -> setMode(Mode.SCALE));
        eventBus.subscribe(EventType.CONTROL_ROTATE_MODEL_CLICK.name(), (e) -> setMode(Mode.ROTATE));
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    public void onMousePressed(MouseEvent event) {
        switch (mode) {
            case MOVE:
                Node n = event.getPickResult().getIntersectedNode();
                if(!(n instanceof MeshView)) { return; }

                MeshView mv = (MeshView) n;
                break;
        }
    }

    private void activate(Event event) {
        if(onActivated != null) { onActivated.handle(event); }
    }

    public void setOnActivated(EventHandler<Event> eventHandler) {
        this.onActivated = eventHandler;
    }

    private enum Mode {
        ROTATE, SCALE, MOVE
    }
}
