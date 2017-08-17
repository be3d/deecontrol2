package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.NumberField;
import com.ysoft.dctrl.ui.dialog.RetentionFileChooser;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 21.4.2017.
 */

@Controller
public class MainPanelController extends LocalizableController implements Initializable {
    @FXML Button add;

    @FXML Button group;
    @FXML Button ungroup;

    @FXML Button center;
    @FXML Button left;
    @FXML Button right;
    @FXML Button front;
    @FXML Button back;

    @FXML Button resetView;
    @FXML Button topView;

    private RetentionFileChooser retentionFileChooser;

    @Autowired
    public MainPanelController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context, RetentionFileChooser retentionFileChooser) {
        super(localizationService, eventBus, context);
        this.retentionFileChooser = retentionFileChooser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setOnAction(event -> {
            File f = retentionFileChooser.showOpenDialog(root.getScene().getWindow(), new FileChooser.ExtensionFilter("3D models", "*.STL", "*.stl"));
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        resetView.setOnAction(event -> eventBus.publish(new Event(EventType.RESET_VIEW.name())));

        center.setOnAction(event -> eventBus.publish(new Event(EventType.CENTER_SELECTED_MODEL.name())));
        left.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_LEFT_SELECTED_MODEL.name())));
        right.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_RIGHT_SELECTED_MODEL.name())));
        front.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_FRONT_SELECTED_MODEL.name())));
        back.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_BACK_SELECTED_MODEL.name())));

        group.setOnAction(event -> eventBus.publish(new Event(EventType.EDIT_GROUP.name())));
        ungroup.setOnAction(event -> eventBus.publish(new Event(EventType.EDIT_UNGROUP.name())));

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> {
            ungroup.setDisable(!(e.getData() instanceof MeshGroup));
            group.setDisable(true);
        });
        eventBus.subscribe(EventType.MODEL_MULTISELECTION.name(), (e) -> {
            ungroup.setDisable(true);
            group.setDisable(false);
        });
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            topView.setVisible(e.getData() == SceneMode.GCODE);
            topView.setManaged(e.getData() == SceneMode.GCODE);
        });

        super.initialize(location, resources);
    }
}
