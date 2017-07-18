package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
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
    @FXML Button add;

    @FXML Button center;
    @FXML Button left;
    @FXML Button right;
    @FXML Button front;
    @FXML Button back;

    @FXML Button resetView;

    public MainPanelController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setOnAction(event -> {
            final FileChooser dialog = new FileChooser();
            File f = dialog.showOpenDialog(root.getScene().getWindow());
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        resetView.setOnAction(event -> {
            eventBus.publish(new Event(EventType.RESET_VIEW.name()));
        });

        center.setOnAction(event -> {eventBus.publish(new Event(EventType.CENTER_SELECTED_MODEL.name()));});
        left.setOnAction(event -> {eventBus.publish(new Event(EventType.ALIGN_LEFT_SELECTED_MODEL.name()));});
        right.setOnAction(event -> {eventBus.publish(new Event(EventType.ALIGN_RIGHT_SELECTED_MODEL.name()));});
        front.setOnAction(event -> {eventBus.publish(new Event(EventType.ALIGN_FRONT_SELECTED_MODEL.name()));});
        back.setOnAction(event -> {eventBus.publish(new Event(EventType.ALIGN_BACK_SELECTED_MODEL.name()));});

        super.initialize(location, resources);
    }
}
