package com.ysoft.dctrl.editor.action;

import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 6.9.2017.
 */
public class DeleteModelAction implements Action {
    private final Consumer<List<SceneMesh>> deleteModel;
    private final Consumer<List<SceneMesh>> addModel;
    private final List<SceneMesh> mesh;

    public DeleteModelAction(Consumer<List<SceneMesh>> deleteModel, Consumer<List<SceneMesh>> addModel, List<SceneMesh> mesh) {
        this.deleteModel = deleteModel;
        this.addModel = addModel;
        this.mesh = mesh;
    }

    @Override
    public void undo() {
        addModel.accept(mesh);
    }

    @Override
    public void redo() {
        deleteModel.accept(mesh);
    }
}
