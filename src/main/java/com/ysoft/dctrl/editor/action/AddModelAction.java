package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;

/**
 * Created by pilar on 6.9.2017.
 */
public class AddModelAction implements Action {
    private final EditSceneGraph editSceneGraph;
    private final ExtendedMesh mesh;

    public AddModelAction(EditSceneGraph editSceneGraph, ExtendedMesh mesh) {
        this.editSceneGraph = editSceneGraph;
        this.mesh = mesh;
    }

    @Override
    public void undo() {
        editSceneGraph.deleteModel(mesh);
    }

    @Override
    public void redo() {
        editSceneGraph.addMesh(mesh);
    }
}
