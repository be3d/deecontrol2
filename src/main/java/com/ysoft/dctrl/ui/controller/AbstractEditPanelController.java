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

    // Mouse drag along x axis increments the field value proportionally to NumberField.MouseDragSensitivity().
    // The Y position of the mouse while dragging applies a penalty to that increment. This effectively
    //  means, the further mouse is in Y from the drag start, the more delicate change is applied to the field value.
    private static double MOUSE_DRAG_Y_DEAD_AREA = 100; // area around 0 in y that is insensitive to this point
    private static double MOUSE_DRAG_Y_SENSITIVITY = 0.002; // defines rate of increasing increment penalty
    private static double MOUSE_DRAG_Y_MAX_INCREMENT_PENALTY = 0.05;

    private double mouseDragLastX;
    private double mouseDragInitialY;

    public AbstractEditPanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
        this.sceneGraph = sceneGraph;

        mouseDragInitialY = 0;
        mouseDragLastX = 0;
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
            mouseDragLastX = e.getSceneX();
            mouseDragInitialY = e.getSceneY();
        });
        field.setOnMouseDragged(e -> {
            double dx = e.getSceneX()-mouseDragLastX;
            mouseDragLastX = e.getSceneX();

            double ym = e.getScreenY()-mouseDragInitialY;
            double y0 = MOUSE_DRAG_Y_DEAD_AREA;

            double sx = field.getMouseDragSensitivity();
            double sy = MOUSE_DRAG_Y_SENSITIVITY;

            double v0 = field.getValue();
            double v1;

            if(Math.abs(ym)>y0) {
                v1 = v0 + sx*dx*Math.max(1 - sy*Math.abs(ym - y0), MOUSE_DRAG_Y_MAX_INCREMENT_PENALTY);
            } else {
                v1 = v0 + sx*dx;
            }

            field.setValue(v1);
            consumer.accept(v1, item);
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

    public abstract void onReset();
}
