package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.Tool;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 21.3.2017.
 */

@Controller
public class ControlPanelController extends LocalizableController implements Initializable {
    //@FXML Button add;

    @FXML Tool move;
    @FXML Tool scale;
    @FXML Tool rotate;

    //@FXML Button export;

    private SceneGraph sceneGraph;
    private Tool selected;

    @Autowired
    public ControlPanelController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext deeControlContext, SceneGraph sceneGraph) {
        super(localizationResource, eventBus, deeControlContext);
        this.sceneGraph = sceneGraph;
        this.selected = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //add.setOnAction(event -> {
        //    final FileChooser dialog = new FileChooser();
        //    File f = dialog.showOpenDialog(null);
        //    if(f == null) return;
        //    eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        //});

        //export.setOnAction(event -> {
        //    SceneExporter sceneExporter = new SceneExporter(eventBus, deeControlContext);
        //    sceneExporter.exportScene(sceneGraph, "c:/tmp/test.stl");
        //});

        move.setOnAction(event -> handleClick(move, EventType.CONTROL_MOVE_MODEL_CLICK));
        scale.setOnAction(event -> handleClick(scale, EventType.CONTROL_SCALE_MODEL_CLICK));
        rotate.setOnAction(event -> handleClick(rotate, EventType.CONTROL_ROTATE_MODEL_CLICK));
        super.initialize(location, resources);
    }

    private void handleClick(Tool tool, EventType eventType) {
        selectTool(tool);
        eventBus.publish(new Event(eventType.name()));
    }

    public void selectTool(Tool tool) {
        if(selected != null) selected.setSelected(false);

        selected = tool;

        if(selected != null) selected.setSelected(true);
    }
}
