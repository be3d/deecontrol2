package com.ysoft.dctrl.editor.action;

import java.util.function.Consumer;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 6.9.2017.
 */
public class AddModelAction implements Action {
    private final Consumer<SceneMesh> addModel;
    private final Consumer<SceneMesh> deleteModel;
    private final ExtendedMesh mesh;

    public AddModelAction(Consumer<SceneMesh> addModel, Consumer<SceneMesh> deleteModel, ExtendedMesh mesh) {
        this.addModel = addModel;
        this.deleteModel = deleteModel;
        this.mesh = mesh;
    }

    @Override
    public void undo() {
        deleteModel.accept(mesh);
    }

    @Override
    public void redo() {
        addModel.accept(mesh);
    }
}
