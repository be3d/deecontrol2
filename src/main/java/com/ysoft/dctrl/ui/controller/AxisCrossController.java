package com.ysoft.dctrl.ui.controller;


import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.AxisCross;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

@Controller
public class AxisCrossController extends AbstractController implements Initializable {
    @FXML private AnchorPane root;
    @FXML private Label xLabel;
    @FXML private Label yLabel;
    @FXML private Label zLabel;

    private final AxisCross axisCross;

    public AxisCrossController(AxisCross axisCross, SceneGraph sceneGraph, EventBus eventBus, DeeControlContext deeControlContext) {
        super(eventBus, deeControlContext);
        this.axisCross = axisCross;
        axisCross.setRefCamera(sceneGraph.getCameraGroup());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        axisCross.init(root, (p) -> handleLabel(p, xLabel), (p) -> handleLabel(p, yLabel), (p) -> handleLabel(p, zLabel));
    }

    private void handleLabel(Point2D point, Label label) {
        double xOffset = 10 - Math.abs(point.getX()*2 - 1) * 10;
        double x = point.getX()*(root.getWidth() - label.getWidth()) + xOffset;
        double y = point.getY()*(root.getHeight() - label.getHeight());
        AnchorPane.setTopAnchor(label, y);
        AnchorPane.setLeftAnchor(label, x);
    }
}
