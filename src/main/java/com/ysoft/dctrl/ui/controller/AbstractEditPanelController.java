package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by pilar on 10.4.2017.
 */
public abstract class AbstractEditPanelController extends LocalizableController {
    private enum Item { X,Y,Z }

    protected SceneGraph sceneGraph;

    @FXML protected TextField x;
    @FXML protected TextField y;
    @FXML protected TextField z;

    @FXML protected Button reset;

    public AbstractEditPanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(localizationResource, eventBus, context);
        this.sceneGraph = sceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) refresh();
        });

        x.textProperty().addListener((observable, oldValue, newValue) -> onChange(newValue, Item.X));
        y.textProperty().addListener((observable, oldValue, newValue) -> onChange(newValue, Item.Y));
        z.textProperty().addListener((observable, oldValue, newValue) -> onChange(newValue, Item.Z));

        reset.setOnMouseClicked((e) -> onReset());

        root.setOnMousePressed(Event::consume);

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> refresh());

        super.initialize(location, resources);
    }

    public abstract void refresh();

    private void onChange(String newValue, Item item) {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) return;
        switch (item) {
            case X:
                onXChange(mesh, newValue);
                break;
            case Y:
                onYChange(mesh, newValue);
                break;
            case Z:
                onZChange(mesh, newValue);
                break;
        }
    }

    public abstract void onXChange(SceneMesh mesh, String newValue);
    public abstract void onYChange(SceneMesh mesh, String newValue);
    public abstract void onZChange(SceneMesh mesh, String newValue);
    public abstract void onReset();
}
