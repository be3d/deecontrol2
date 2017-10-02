package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.Tool;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

/**
 * Created by pilar on 21.3.2017.
 */

@Controller
public class ControlPanelController extends LocalizableController implements Initializable {
    @FXML Tool move;
    @FXML Tool scale;
    @FXML Tool rotate;

    private Tool selected;

    @Autowired
    public ControlPanelController(LocalizationService localizationService, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationService, eventBus, deeControlContext);
        this.selected = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.addEventHandler(MouseEvent.ANY, javafx.event.Event::consume);

        move.setOnAction(event -> handleClick(move, EventType.CONTROL_MOVE_MODEL_CLICK));
        scale.setOnAction(event -> handleClick(scale, EventType.CONTROL_SCALE_MODEL_CLICK));
        rotate.setOnAction(event -> handleClick(rotate, EventType.CONTROL_ROTATE_MODEL_CLICK));

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), e -> {
            if(e.getData() == null) { selectTool(null); }
            else if(selected == null) { handleClick(move, EventType.CONTROL_MOVE_MODEL_CLICK); }
        });
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), e -> root.setVisible(e.getData() == SceneMode.EDIT));

        super.initialize(location, resources);
    }

    private void handleClick(Tool tool, EventType eventType) {
        selectTool(tool);
        eventBus.publish(new Event(eventType.name()));
    }

    public void selectTool(Tool tool) {
        if(selected != null) selected.setSelected(false);
        if(selected == tool) { tool = null; }
        selected = tool;
        if(selected != null) selected.setSelected(true);
    }
}
