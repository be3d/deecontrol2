package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 8.9.2017.
 */
public class ModelScaleAction implements TransformAction {
    private final SceneMesh mesh;
    private final Point3D oldScale;
    private final Point3D newScale;

    public ModelScaleAction(SceneMesh mesh, Point3D oldScale, Point3D newScale) {
        this.mesh = mesh;
        this.oldScale = oldScale;
        this.newScale = newScale;
    }

    @Override
    public void undo() {
        mesh.setScale(oldScale);
    }

    @Override
    public void redo() {
        mesh.setScale(newScale);
    }
}
