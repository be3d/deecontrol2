package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;

/**
 * Created by pilar on 12.9.2017.
 */
public class ModelTransformAction implements Action {
    private final TransformAction[] actions;

    public ModelTransformAction(TransformAction... actions) {
        this.actions = actions;
    }

    @Override
    public void undo() {
        for(TransformAction a : actions) {
            a.undo();
        }
    }

    @Override
    public void redo() {
        for(TransformAction a : actions) {
            a.redo();
        }
    }
}
