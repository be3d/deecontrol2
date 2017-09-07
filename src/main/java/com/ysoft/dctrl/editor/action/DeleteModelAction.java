package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 6.9.2017.
 */
public class DeleteModelAction implements Action {
    private final EditSceneGraph editSceneGraph;
    private final SceneMesh mesh;

    public DeleteModelAction(EditSceneGraph editSceneGraph, SceneMesh mesh) {
        this.editSceneGraph = editSceneGraph;
        this.mesh = mesh;
    }

    @Override
    public void undo() {
        editSceneGraph.addMesh(mesh);
    }

    @Override
    public void redo() {
        editSceneGraph.deleteModel(mesh);
    }
}
