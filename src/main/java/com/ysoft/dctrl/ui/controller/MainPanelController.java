package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
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

    public MainPanelController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setOnAction(event -> {
            final FileChooser dialog = new FileChooser();
            File f = dialog.showOpenDialog(null);
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        super.initialize(location, resources);
    }
}
