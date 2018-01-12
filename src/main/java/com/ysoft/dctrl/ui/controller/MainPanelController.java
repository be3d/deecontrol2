package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.editor.control.CameraType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.action.ActionStack;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.dialog.RetentionFileChooser;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 21.4.2017.
 */

@Controller
public class MainPanelController extends LocalizableController implements Initializable {
    private static final int GROUP_BIT = 0;
    private static final int UNGROUP_BIT = 1;
    private static final int UNDO_BIT = 2;
    private static final int REDO_BIT = 3;
    private static final int SELECTION_EMPTY_BIT = 4;

    @FXML Button add;

    @FXML Button group;
    @FXML Button ungroup;

    @FXML Button undo;
    @FXML Button redo;

    @FXML Button center;
    @FXML Button left;
    @FXML Button right;
    @FXML Button front;
    @FXML Button back;

    @FXML Button resetView;
    @FXML Button topView;
    @FXML Button perspectiveOn;
    @FXML Button perspectiveOff;

    private int disabledMap;

    private final RetentionFileChooser retentionFileChooser;
    private final ActionStack actionStack;

    @Autowired
    public MainPanelController(LocalizationService localizationService,
                               EventBus eventBus,
                               DeeControlContext context,
                               RetentionFileChooser retentionFileChooser,
                               ActionStack actionStack) {
        super(localizationService, eventBus, context);
        this.retentionFileChooser = retentionFileChooser;
        this.actionStack = actionStack;
        this.disabledMap = 0xff;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setOnAction(event -> {
            File f = retentionFileChooser.showOpenDialog(root.getScene().getWindow(), new FileChooser.ExtensionFilter("3D models", "*.STL", "*.stl"));
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        resetView.setOnAction(event -> eventBus.publish(new Event(EventType.RESET_VIEW.name())));
        topView.setOnAction(event -> eventBus.publish(new Event(EventType.VIEW_TOP.name())));
        perspectiveOn.setOnAction(event -> eventBus.publish(new Event(EventType.SET_CAMERA.name(), CameraType.PERSPECTIVE)));
        perspectiveOff.setOnAction(event -> eventBus.publish(new Event(EventType.SET_CAMERA.name(), CameraType.PARALLEL)));

        center.setOnAction(event -> eventBus.publish(new Event(EventType.CENTER_SELECTED_MODEL.name())));
        left.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_LEFT_SELECTED_MODEL.name())));
        right.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_RIGHT_SELECTED_MODEL.name())));
        front.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_FRONT_SELECTED_MODEL.name())));
        back.setOnAction(event -> eventBus.publish(new Event(EventType.ALIGN_BACK_SELECTED_MODEL.name())));

        group.setOnAction(event -> eventBus.publish(new Event(EventType.EDIT_GROUP.name())));
        ungroup.setOnAction(event -> eventBus.publish(new Event(EventType.EDIT_UNGROUP.name())));

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> {
            setDisabledBit(true, GROUP_BIT);
            setDisabledBit(!(e.getData() instanceof MeshGroup), UNGROUP_BIT);
            ungroup.setDisable(getDisabledBit(UNGROUP_BIT));
            group.setDisable(getDisabledBit(GROUP_BIT));

            SceneMesh m = (SceneMesh)e.getData();
            setDisabledBit(m == null, SELECTION_EMPTY_BIT);
            center.setDisable(getDisabledBit(SELECTION_EMPTY_BIT));
            left.setDisable(getDisabledBit(SELECTION_EMPTY_BIT));
            right.setDisable(getDisabledBit(SELECTION_EMPTY_BIT));
            front.setDisable(getDisabledBit(SELECTION_EMPTY_BIT));
            back.setDisable(getDisabledBit(SELECTION_EMPTY_BIT));
        });

        eventBus.subscribe(EventType.MODEL_MULTISELECTION.name(), (e) -> {
            setDisabledBit(true, UNGROUP_BIT);
            setDisabledBit(false, GROUP_BIT);

            ungroup.setDisable(getDisabledBit(UNGROUP_BIT));
            group.setDisable(getDisabledBit(GROUP_BIT));
        });
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            boolean gcodeMode = e.getData() == SceneMode.GCODE;
            add.setDisable(gcodeMode);
            center.setDisable(gcodeMode || getDisabledBit(SELECTION_EMPTY_BIT));
            left.setDisable(gcodeMode || getDisabledBit(SELECTION_EMPTY_BIT));
            right.setDisable(gcodeMode || getDisabledBit(SELECTION_EMPTY_BIT));
            front.setDisable(gcodeMode || getDisabledBit(SELECTION_EMPTY_BIT));
            back.setDisable(gcodeMode || getDisabledBit(SELECTION_EMPTY_BIT));

            group.setDisable(gcodeMode || getDisabledBit(GROUP_BIT));
            ungroup.setDisable(gcodeMode || getDisabledBit(UNGROUP_BIT));
            undo.setDisable(gcodeMode || getDisabledBit(UNDO_BIT));
            redo.setDisable(gcodeMode || getDisabledBit(REDO_BIT));
        });

        undo.setOnAction(event -> actionStack.undo());
        redo.setOnAction(event -> actionStack.redo());

        eventBus.subscribe(EventType.UNDO_EMPTY.name(), (e) -> {
            setDisabledBit(true, UNDO_BIT);
            undo.setDisable(getDisabledBit(UNDO_BIT));
        });
        eventBus.subscribe(EventType.UNDO_NOT_EMPTY.name(), (e) -> {
            setDisabledBit(false, UNDO_BIT);
            undo.setDisable(getDisabledBit(UNDO_BIT));
        });
        eventBus.subscribe(EventType.REDO_EMPTY.name(), (e) -> {
            setDisabledBit(true, REDO_BIT);
            redo.setDisable(getDisabledBit(REDO_BIT));
        });
        eventBus.subscribe(EventType.REDO_NOT_EMPTY.name(), (e) -> {
            setDisabledBit(false, REDO_BIT);
            redo.setDisable(getDisabledBit(REDO_BIT));
        });

        eventBus.subscribe(EventType.SET_CAMERA.name(), (e) -> {
            CameraType c = (CameraType)e.getData();
            perspectiveOn.setVisible(c == CameraType.PARALLEL);
            perspectiveOn.setManaged(c == CameraType.PARALLEL);
            perspectiveOff.setVisible(c == CameraType.PERSPECTIVE);
            perspectiveOff.setManaged(c == CameraType.PERSPECTIVE);
        });

        super.initialize(location, resources);
    }

    private void setDisabledBit(boolean value, int bit) {
        disabledMap = (value) ? disabledMap | (1<<bit) : disabledMap & ~(1<<bit);
    }

    private boolean getDisabledBit(int bit) {
        return (disabledMap & (1<<bit)) > 0;
    }
}
