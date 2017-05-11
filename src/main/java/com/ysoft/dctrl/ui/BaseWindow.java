package com.ysoft.dctrl.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.ui.factory.ControlMenuFactory;
import com.ysoft.dctrl.ui.factory.ControlPanelFactory;
import com.ysoft.dctrl.ui.factory.EditorCanvasFactory;
import com.ysoft.dctrl.ui.factory.MainPanelFactory;
import com.ysoft.dctrl.ui.factory.MenuBarFactory;
import com.ysoft.dctrl.utils.KeyEventPropagator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by pilar on 29.3.2017.
 */
@Component
public class BaseWindow {
    private KeyEventPropagator keyEventPropagator;

    private Region menuBar;
    private Region editorCanvas;
    private Region mainPanel;
    private Region controlMenu;

    @Autowired
    public BaseWindow(KeyEventPropagator keyEventPropagator,
                      MenuBarFactory menuBarFactory,
                      MainPanelFactory mainPanelFactory,
                      EditorCanvasFactory editorCanvasFactory,
                      ControlMenuFactory controlMenuFactory
    ) {
        this.keyEventPropagator = keyEventPropagator;
        menuBar = menuBarFactory.buildMenuBar();
        mainPanel = mainPanelFactory.buildMainPanel();
        editorCanvas = editorCanvasFactory.buildEditorCanvas();
        controlMenu = controlMenuFactory.buildControlMenu();
    }

    public void composeWindow(Stage stage) {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);

        // TODO add config to application - get default values from config
        //
        Scene scene = new Scene(root, 1366, 768);
        scene.setOnKeyPressed(keyEventPropagator::keyPressed);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());

        AnchorPane canvasPane = new AnchorPane();
        canvasPane.maxHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty()).subtract(mainPanel.heightProperty()));
        canvasPane.prefHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty()).subtract(mainPanel.heightProperty()));

        setAnchors(controlMenu, 0.0, null, 0.0, 0.0);
        setAnchors(editorCanvas, 0.0, 0.0, 0.0, null);
        setAnchors(mainPanel, 0.0, 0.0, null, 0.0);
;
        editorCanvas.prefHeightProperty().bind(canvasPane.prefHeightProperty());
        editorCanvas.prefWidthProperty().bind(root.widthProperty().subtract(controlMenu.widthProperty()));

        canvasPane.getChildren().addAll(editorCanvas, controlMenu);

        root.getChildren().addAll(menuBar, mainPanel, canvasPane);
        stage.setScene(scene);
    }

    private static void setAnchors(Node el, Double top, Double left, Double bottom, Double right) {
        if(top    != null) { AnchorPane.setTopAnchor(   el, top); }
        if(left   != null) { AnchorPane.setLeftAnchor(  el, left); }
        if(bottom != null) { AnchorPane.setBottomAnchor(el, bottom); }
        if(right  != null) { AnchorPane.setRightAnchor( el, right); }
    }

}
