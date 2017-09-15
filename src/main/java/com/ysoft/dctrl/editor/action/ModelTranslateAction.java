package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 8.9.2017.
 */
public class ModelTranslateAction implements TransformAction {
    private final SceneMesh mesh;
    private final Point3D oldPosition;
    private final Point3D newPosition;

    public ModelTranslateAction(SceneMesh mesh, Point3D oldPosition, Point3D newPosition) {
        this.mesh = mesh;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void undo() {
        mesh.setPosition(oldPosition);
    }

    @Override
    public void redo() {
        mesh.setPosition(newPosition);
    }
}
