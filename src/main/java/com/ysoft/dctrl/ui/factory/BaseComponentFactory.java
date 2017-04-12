package com.ysoft.dctrl.ui.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.SpringFXMLLoader;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by pilar on 20.3.2017.
 */

@Component
public class BaseComponentFactory implements
        MenuBarFactory,
        ControlMenuFactory,
        EditorCanvasFactory,
        RotationPanelFactory,
        MovePanelFactory,
        ScalePanelFactory,
        BottomPanelWrapperFactory
{
    private final SpringFXMLLoader loader;

    @Autowired
    public BaseComponentFactory(SpringFXMLLoader loader) {
        this.loader = loader;
    }

    @Override
    public MenuBar buildMenuBar() {
        return (MenuBar) loader.load("/view/menu_bar.fxml");
    }

    @Override
    public AnchorPane buildControlMenu() {
        return (AnchorPane) loader.load("/view/control_menu.fxml");
    }

    @Override
    public AnchorPane buildEditorCanvas() {
        AnchorPane canvas = (AnchorPane) loader.load("/view/editor_canvas.fxml");
        canvas.getChildren().addAll(buildBottomPanelWrapper());
        return canvas;
    }

    @Override
    public VBox buildRotationPanel() {
        return (VBox) loader.load("/view/rotation_panel.fxml");
    }

    @Override
    public VBox buildMovePanel() {
        return (VBox) loader.load("/view/move_panel.fxml");
    }

    @Override
    public VBox buildScalePanel() { return (VBox) loader.load("/view/scale_panel.fxml"); }

    @Override
    public StackPane buildBottomPanelWrapper() {
        return (StackPane) loader.load("/view/bottom_panel_wrapper.fxml");
    }
}
