package com.ysoft.dctrl.ui;

import com.ysoft.dctrl.safeq.job.SnapShotCreator;
import com.ysoft.dctrl.ui.factory.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ysoft.dctrl.utils.KeyEventPropagator;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;

/**
 * Created by pilar on 29.3.2017.
 */
@Component
public class BaseWindow {
    private KeyEventPropagator keyEventPropagator;
    private DialogManager dialogManager;
    private NotificationManager notificationManager;
    private SnapShotCreator snapShotCreator;

    private Region menuBar;
    private Region editorCanvas;
    private Region mainPanel;
    private Region slicerPanel;
    private Region gcodePanel;
    private Region gCodeLayerControlPanel;
    private Region tooltipWrapper;

    @Autowired
    public BaseWindow(KeyEventPropagator keyEventPropagator,
                      DialogManager dialogManager,
                      NotificationManager notificationManager,
                      SnapShotCreator snapShotCreator,
                      MenuBarFactory menuBarFactory,
                      MainPanelFactory mainPanelFactory,
                      EditorCanvasFactory editorCanvasFactory,
                      SlicerPanelFactory slicerPanelFactory,
                      GCodePanelFactory gcodePanelFactory,
                      GCodeLayerSliderFactory gCodeLayerSliderFactory,
                      TooltipWrapperFactory tooltipWrapperFactory
    ) {
        this.keyEventPropagator = keyEventPropagator;
        this.dialogManager = dialogManager;
        this.notificationManager = notificationManager;
        this.snapShotCreator = snapShotCreator;
        menuBar = menuBarFactory.buildMenuBar();
        mainPanel = mainPanelFactory.buildMainPanel();
        editorCanvas = editorCanvasFactory.buildEditorCanvas();
        slicerPanel = slicerPanelFactory.buildSlicerPanel();
        gcodePanel = gcodePanelFactory.buildGCodePanel();
        gCodeLayerControlPanel = gCodeLayerSliderFactory.buildGCodeLayerSlider();
        tooltipWrapper = tooltipWrapperFactory.buildTooltipWrapper();
    }

    public void composeWindow(Stage stage) {
        AnchorPane root = new AnchorPane();
        root.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

        VBox content = new VBox();

        setAnchors(content, 0.0, 0.0, 0.0, 0.0);
        setAnchors(dialogManager.getNode(), 0.0, 0.0, 0.0, 0.0);
        setAnchors(tooltipWrapper, 0.0,0.0,0.0,282.0);

        root.getChildren().addAll(content, dialogManager.getNode(), snapShotCreator.getNode());
        root.getChildren().addAll(tooltipWrapper);

        content.setBackground(Background.EMPTY);

        // TODO add config to application - get default values from config
        //
        Scene scene = new Scene(root, 1366, 768);
        scene.setOnKeyPressed(keyEventPropagator::keyPressed);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());

        AnchorPane canvasPane = new AnchorPane();
        canvasPane.maxHeightProperty().bind(content.heightProperty().subtract(menuBar.heightProperty()).subtract(mainPanel.heightProperty()));
        canvasPane.prefHeightProperty().bind(content.heightProperty().subtract(menuBar.heightProperty()).subtract(mainPanel.heightProperty()));

        setAnchors(slicerPanel, 0.0, null, 0.0, 0.0);
        setAnchors(gcodePanel, 0.0, null, 0.0, 0.0);
        setAnchors(editorCanvas, 0.0, 0.0, 0.0, null);
        setAnchors(mainPanel, 0.0, 0.0, null, 0.0);
        setAnchors(gCodeLayerControlPanel, 20.0, null, null, 305.0);

        editorCanvas.prefHeightProperty().bind(canvasPane.prefHeightProperty());
        editorCanvas.prefWidthProperty().bind(root.widthProperty().subtract(slicerPanel.widthProperty()));

        ((AnchorPane) editorCanvas).getChildren().add(notificationManager.getNode());
        canvasPane.getChildren().addAll(slicerPanel, gcodePanel, editorCanvas, gCodeLayerControlPanel);
        content.getChildren().addAll(menuBar, mainPanel, canvasPane);

        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setScene(scene);

    }

    private static void setAnchors(Node el, Double top, Double left, Double bottom, Double right) {
        if(top    != null) { AnchorPane.setTopAnchor(   el, top); }
        if(left   != null) { AnchorPane.setLeftAnchor(  el, left); }
        if(bottom != null) { AnchorPane.setBottomAnchor(el, bottom); }
        if(right  != null) { AnchorPane.setRightAnchor( el, right); }
    }

}
