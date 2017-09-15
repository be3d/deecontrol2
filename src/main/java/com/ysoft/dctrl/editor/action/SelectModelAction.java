package com.ysoft.dctrl.editor.action;

import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.SceneMesh;

/**
 * Created by pilar on 14.9.2017.
 */
public class SelectModelAction implements Action {
    private final Consumer<SceneMesh> selectModel;
    private final Consumer<List<SceneMesh>> setOldSelection;
    private final SceneMesh model;
    private final List<SceneMesh> oldSelection;

    public SelectModelAction(Consumer<SceneMesh> selectModel, Consumer<List<SceneMesh>> setOldSelection, SceneMesh model, List<SceneMesh> oldSelection) {
        this.selectModel = selectModel;
        this.setOldSelection = setOldSelection;
        this.model = model;
        this.oldSelection = oldSelection;
    }

    @Override
    public void undo() {
        setOldSelection.accept(oldSelection);
    }

    @Override
    public void redo() {
        selectModel.accept(model);
    }
}
