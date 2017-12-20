package com.ysoft.dctrl.ui.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.SpringFXMLLoader;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by pilar on 20.3.2017.
 */

@Component
public class BaseFactory implements
        MenuBarFactory,
        ControlPanelFactory,
        EditorCanvasFactory,
        AxisCrossFactory,
        RotationPanelFactory,
        MovePanelFactory,
        ScalePanelFactory,
        EditPanelWrapperFactory,
        MainPanelFactory,
        NoModelPanelFactory,
        SlicerPanelFactory,
        GCodePanelFactory,
        GCodeLayerSliderFactory,
        NotificationWrapperFactory,
        TooltipWrapperFactory {
    private final SpringFXMLLoader loader;

    @Autowired
    public BaseFactory(SpringFXMLLoader loader) {
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
        canvas.getChildren().addAll(buildEditPanelWrapper(), buildControlPanel(), buildNoModelPanel(), buildAxissCross());
        return canvas;
    }

    @Override
    public AnchorPane buildAxissCross() {
        return (AnchorPane) loader.load("/view/axis_cross.fxml");
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
    public StackPane buildEditPanelWrapper() {
        return (StackPane) loader.load("/view/edit_panel_wrapper.fxml");
    }

    @Override
    public AnchorPane buildMainPanel() {
        return (AnchorPane) loader.load("/view/main_panel.fxml");
    }

    @Override
    public StackPane buildNoModelPanel() { return (StackPane) loader.load("/view/no_model_panel.fxml"); }

    @Override
    public AnchorPane buildSlicerPanel() {
        return (AnchorPane) loader.load("/view/slicer_panel.fxml");
    }

    @Override
    public AnchorPane buildGCodePanel(){return (AnchorPane) loader.load("/view/gcode_panel.fxml"); }

    @Override
    public AnchorPane buildGCodeLayerSlider(){return (AnchorPane) loader.load("/view/gcode_layer_picker.fxml") ;}

    @Override
    public StackPane buildNotificationWrapper() {
        return (StackPane) loader.load("/view/notification_wrapper.fxml");
    }

    @Override
    public AnchorPane buildTooltipWrapper() { return (AnchorPane) loader.load("/view/controlMenuTooltipWrapper.fxml"); }
}
