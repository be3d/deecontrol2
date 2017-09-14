package com.ysoft.dctrl.editor.action;

import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 14.9.2017.
 */
public class UngroupModelAction implements Action {
    private final Consumer<MeshGroup> ungroupModel;
    private final Consumer<List<SceneMesh>> groupModels;
    private final List<SceneMesh> meshes;

    public UngroupModelAction(Consumer<MeshGroup> ungroupModel, Consumer<List<SceneMesh>> groupModels, List<SceneMesh> meshes) {
        this.ungroupModel = ungroupModel;
        this.groupModels = groupModels;
        this.meshes = meshes;
    }

    @Override
    public void undo() {
        groupModels.accept(meshes);
    }

    @Override
    public void redo() {
        ungroupModel.accept(meshes.get(0).getGroup());
    }
}
