package com.ysoft.dctrl.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.ui.factory.BottomPanelWrapperFactory;
import com.ysoft.dctrl.ui.factory.ControlMenuFactory;
import com.ysoft.dctrl.ui.factory.EditorCanvasFactory;
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
    private Region controlMenu;
    private Region editorCanvas;

    @Autowired
    public BaseWindow(KeyEventPropagator keyEventPropagator,
                      MenuBarFactory menuBarFactory,
                      ControlMenuFactory controlMenuFactory,
                      EditorCanvasFactory editorCanvasFactory
    ) {
        this.keyEventPropagator = keyEventPropagator;
        menuBar = menuBarFactory.buildMenuBar();
        controlMenu = controlMenuFactory.buildControlMenu();
        editorCanvas = editorCanvasFactory.buildEditorCanvas();
    }

    public void composeWindow(Stage stage) {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);

        // TODO add config to application - get default values from config
        //
        Scene scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(keyEventPropagator::keyPressed);

        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());

        AnchorPane underMenuBar = new AnchorPane();
        underMenuBar.maxHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty()));
        underMenuBar.prefHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty()));

        setAnchors(controlMenu, 0.0, 0.0, 0.0, null);
        setAnchors(editorCanvas, 0.0, null, 0.0, 0.0);

        editorCanvas.prefWidthProperty().bind(underMenuBar.widthProperty().subtract(controlMenu.widthProperty()));
        editorCanvas.prefHeightProperty().bind(underMenuBar.prefHeightProperty());

        underMenuBar.getChildren().addAll(controlMenu, editorCanvas);

        root.getChildren().addAll(menuBar, underMenuBar);
        stage.setScene(scene);
    }

    private static void setAnchors(Node el, Double top, Double left, Double bottom, Double right) {
        if(top    != null) { AnchorPane.setTopAnchor(   el, top); }
        if(left   != null) { AnchorPane.setLeftAnchor(  el, left); }
        if(bottom != null) { AnchorPane.setBottomAnchor(el, bottom); }
        if(right  != null) { AnchorPane.setRightAnchor( el, right); }
    }
}
