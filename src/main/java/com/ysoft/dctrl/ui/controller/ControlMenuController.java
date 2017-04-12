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
public class ControlMenuController extends LocalizableController implements Initializable {
    @FXML Button add;

    @FXML Button move;
    @FXML Button scale;
    @FXML Button rotate;

    @FXML Button export;

    private SceneGraph sceneGraph;

    @Autowired
    public ControlMenuController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext deeControlContext, SceneGraph sceneGraph) {
        super(localizationResource, eventBus, deeControlContext);
        this.sceneGraph = sceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setOnAction(event -> {
            final FileChooser dialog = new FileChooser();
            File f = dialog.showOpenDialog(null);
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        export.setOnAction(event -> {
            SceneExporter sceneExporter = new SceneExporter(eventBus, deeControlContext);
            sceneExporter.exportScene(sceneGraph, "c:/tmp/test.stl");
        });

        move.setOnAction(event -> eventBus.publish(new Event(EventType.CONTROL_MOVE_MODEL_CLICK.name())));
        scale.setOnAction(event -> eventBus.publish(new Event(EventType.CONTROL_SCALE_MODEL_CLICK.name())));
        rotate.setOnAction(event -> eventBus.publish(new Event(EventType.CONTROL_ROTATE_MODEL_CLICK.name())));
        super.initialize(location, resources);
    }
}
