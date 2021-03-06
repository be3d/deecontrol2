package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.factory.MovePanelFactory;
import com.ysoft.dctrl.ui.factory.RotationPanelFactory;
import com.ysoft.dctrl.ui.factory.ScalePanelFactory;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Created by pilar on 7.4.2017.
 */

@Controller
public class EditPanelWrapperController extends AbstractController implements Initializable {
    @FXML
    private StackPane wrapper;

    private Region rotationPanel;
    private Region movePanel;
    private Region scalePanel;

    private Region visiblePanel;

    @Autowired
    public EditPanelWrapperController(
            EventBus eventBus,
            DeeControlContext deeControlContext,
            RotationPanelFactory rotationMenuFactory,
            MovePanelFactory movePanelFactory,
            ScalePanelFactory scalePanelFactory
    ) {
        super(eventBus, deeControlContext);
        rotationPanel = rotationMenuFactory.buildRotationPanel();
        movePanel = movePanelFactory.buildMovePanel();
        scalePanel = scalePanelFactory.buildScalePanel();

        visiblePanel = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rotationPanel.setVisible(false);
        movePanel.setVisible(false);
        scalePanel.setVisible(false);
        wrapper.getChildren().addAll(rotationPanel, movePanel, scalePanel);
        wrapper.setPickOnBounds(false);

        eventBus.subscribe(EventType.CONTROL_MOVE_MODEL_CLICK.name(), (e) -> showPanel(movePanel));
        eventBus.subscribe(EventType.CONTROL_ROTATE_MODEL_CLICK.name(), (e) -> showPanel(rotationPanel));
        eventBus.subscribe(EventType.CONTROL_SCALE_MODEL_CLICK.name(), (e) -> showPanel(scalePanel));

        eventBus.subscribe(EventType.MODEL_SELECTED.name(), (e) -> {
            if(e.getData() == null) { hidePanel(); }
        });

        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), e -> wrapper.setVisible(e.getData() == SceneMode.EDIT));
    }

    public void hidePanel() {
        showPanel(null);
    }

    public void showPanel(Region panel) {
        if(visiblePanel != null) {
            visiblePanel.setVisible(false);
            visiblePanel.setPickOnBounds(false);
        }

        if(visiblePanel == panel) { panel = null; }
        if(panel != null) {
            panel.setVisible(true);
            panel.setPickOnBounds(true);
        }
        visiblePanel = panel;
    }
}
