package com.ysoft.dctrl.editor.action;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 14.9.2017.
 */
public class GroupModelAction implements Action {
    private final Consumer<List<SceneMesh>> groupModels;
    private final Consumer<MeshGroup> ungroupModel;
    private final List<SceneMesh> meshes;

    public GroupModelAction(Consumer<List<SceneMesh>> groupModels, Consumer<MeshGroup> ungroupModel, List<SceneMesh> meshes) {
        this.groupModels = groupModels;
        this.ungroupModel = ungroupModel;
        this.meshes = meshes;
    }

    @Override
    public void undo() {
        ungroupModel.accept(meshes.get(0).getGroup());
    }

    @Override
    public void redo() {
        groupModels.accept(meshes);
    }
}
