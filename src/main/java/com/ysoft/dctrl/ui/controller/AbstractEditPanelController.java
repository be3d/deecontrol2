package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.NumberField;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

/**
 * Created by pilar on 10.4.2017.
 */
public abstract class AbstractEditPanelController extends LocalizableController {
    protected enum Item { X,Y,Z }

    protected EditSceneGraph sceneGraph;

    @FXML protected NumberField x;
    @FXML protected NumberField y;
    @FXML protected NumberField z;

    private double mouseDragInitialX;
    private double mouseDragInitialY;
    private double mouseDragInitialValue;


    public AbstractEditPanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
        this.sceneGraph = sceneGraph;

        mouseDragInitialX = 0;
        mouseDragInitialY = 0;
        mouseDragInitialValue = 0;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) refresh(sceneGraph.getSelected());
        });

        initNumberFieldListener(x, Item.X, this::onChange);
        initNumberFieldListener(y, Item.Y, this::onChange);
        initNumberFieldListener(z, Item.Z, this::onChange);

        root.setOnMousePressed(Event::consume);

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> refresh((SceneMesh) e.getData()));
        eventBus.subscribe(EventType.MODEL_CHANGED.name(), (e) -> refresh((SceneMesh) e.getData()));

        super.initialize(location, resources);
    }

    protected void initNumberFieldListener(NumberField field, Item item, BiConsumer<Double, Item> consumer) {
        field.focusedProperty().addListener((ob, o, n) -> {
            if(!n && !field.isInvalid()) { consumer.accept(field.getValue(), item); }
        });
        field.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) { consumer.accept(field.getValue(), item); }
        });
        field.setOnMousePressed(e -> {
            mouseDragInitialX = e.getSceneX();
            mouseDragInitialY = e.getSceneY();
            mouseDragInitialValue = field.getValue();
        });
        field.setOnMouseDragged(e -> {
            double dx = e.getSceneX()-mouseDragInitialX;
            double dy = e.getSceneY()-mouseDragInitialY;
            double newValue = mouseDragInitialValue + field.getMouseDragScale()*dx/(1+Math.abs(dy)*0.1);
            field.setValue(newValue);
            consumer.accept(field.getValue(), item);
        });
    }

    public abstract void refresh(SceneMesh mesh);

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

   // public abstract void onXDrag(SceneMesh mesh, double newValue);

    public abstract void onReset();
}
