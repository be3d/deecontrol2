package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.NumberField;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * Created by pilar on 10.4.2017.
 */
public abstract class AbstractEditPanelController extends LocalizableController {
    private enum Item { X,Y,Z }

    protected EditSceneGraph sceneGraph;

    @FXML protected NumberField x;
    @FXML protected NumberField y;
    @FXML protected NumberField z;

    public AbstractEditPanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
        this.sceneGraph = sceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) refresh();
        });

        x.textProperty().addListener((observable, oldValue, newValue) -> onChange(x.getValue(), Item.X));
        y.textProperty().addListener((observable, oldValue, newValue) -> onChange(y.getValue(), Item.Y));
        z.textProperty().addListener((observable, oldValue, newValue) -> onChange(z.getValue(), Item.Z));

        root.setOnMousePressed(Event::consume);

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> refresh());

        super.initialize(location, resources);
    }

    public abstract void refresh();

    private void onChange(double newValue, Item item) {
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

    public abstract void onXChange(SceneMesh mesh, double newValue);
    public abstract void onYChange(SceneMesh mesh, double newValue);
    public abstract void onZChange(SceneMesh mesh, double newValue);
    public abstract void onReset();
}
