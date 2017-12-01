package com.ysoft.dctrl.ui.component;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.controller.AbstractController;
import com.ysoft.dctrl.ui.controller.LocalizableController;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

@Controller
public class AboutController extends LocalizableController implements Initializable {
    @FXML
    private Stage stage;

    @FXML
    private Label version;

    @Autowired
    public AboutController(LocalizationService localizationService, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationService, eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        stage.setOnShowing((e) -> {
            version.setText(getMessage("about_version") + " " + deeControlContext.getVersion());
            setPosition();
            stage.requestFocus();
        });

        stage.focusedProperty().addListener((ob, o, n) -> {
            if(!n) { stage.hide(); }
        });
    }

    private void setPosition() {
        Window owner = stage.getOwner();
        if(owner == null) { return; }

        stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth())/2.0);
        stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight())/2.0);
    }
}
