package com.ysoft.dctrl.ui.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.SpringFXMLLoader;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by pilar on 20.3.2017.
 */

@Component
public class BaseComponentFactory implements
        MenuBarFactory,
        ControlPanelFactory,
        EditorCanvasFactory,
        RotationPanelFactory,
        MovePanelFactory,
        ScalePanelFactory,
        BottomPanelWrapperFactory,
        MainPanelFactory,
        NoModelPanelFactory
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
    public AnchorPane buildControlPanel() {
        return (AnchorPane) loader.load("/view/control_panel.fxml");
    }

    @Override
    public AnchorPane buildEditorCanvas() {
        AnchorPane canvas = (AnchorPane) loader.load("/view/editor_canvas.fxml");
        canvas.getChildren().addAll(buildBottomPanelWrapper(), buildControlPanel(), buildNoModelPanel());
        return canvas;
    }

    @Override
    public HBox buildRotationPanel() {
        return (HBox) loader.load("/view/rotation_panel.fxml");
    }

    @Override
    public HBox buildMovePanel() {
        return (HBox) loader.load("/view/move_panel.fxml");
    }

    @Override
    public HBox buildScalePanel() { return (HBox) loader.load("/view/scale_panel.fxml"); }

    @Override
    public StackPane buildBottomPanelWrapper() {
        return (StackPane) loader.load("/view/bottom_panel_wrapper.fxml");
    }

    @Override
    public AnchorPane buildMainPanel() {
        return (AnchorPane) loader.load("/view/main_panel.fxml");
    }

    @Override
    public StackPane buildNoModelPanel() { return (StackPane) loader.load("/view/no_model_panel.fxml"); }
}
