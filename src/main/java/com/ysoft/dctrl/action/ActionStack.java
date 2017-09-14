package com.ysoft.dctrl.action;

import java.util.Stack;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

/**
 * Created by pilar on 6.9.2017.
 */

@Component
public class ActionStack {
    private final Stack<Action> undoStack;
    private final Stack<Action> redoStack;

    private static final Event UNDO_EMPTY = new Event(EventType.UNDO_EMPTY.name());
    private static final Event UNDO_NOT_EMPTY = new Event(EventType.UNDO_NOT_EMPTY.name());
    private static final Event REDO_EMPTY = new Event(EventType.REDO_EMPTY.name());
    private static final Event REDO_NOT_EMPTY = new Event(EventType.REDO_NOT_EMPTY.name());

    private final EventBus eventBus;

    public ActionStack(EventBus eventBus) {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.ADD_ACTION.name(), e -> addAction((Action) e.getData()));
    }

    public void undo() {
        if(undoStack.isEmpty()) { return; }

        Action a = undoStack.pop();
        a.undo();
        redoStack.add(a);
        if(undoStack.isEmpty()) { eventBus.publish(UNDO_EMPTY); }
        if(redoStack.size() == 1) { eventBus.publish(REDO_NOT_EMPTY); }
    }

    public void redo() {
        if(redoStack.isEmpty()) { return; }
        Action a = redoStack.pop();
        a.redo();
        undoStack.add(a);
        if(redoStack.isEmpty()) { eventBus.publish(REDO_EMPTY); }
        if(undoStack.size() == 1) { eventBus.publish(UNDO_NOT_EMPTY); }
    }

    private void addAction(Action action) {
        int redoSize = redoStack.size();
        undoStack.add(action);
        redoStack.clear();
        if(undoStack.size() == 1) { eventBus.publish(UNDO_NOT_EMPTY); }
        if(redoSize > 0) { eventBus.publish(REDO_EMPTY); }
    }
}
