package com.ysoft.dctrl.ui.component;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.controller.AbstractController;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

@Controller
public class AboutController extends AbstractController implements Initializable {
    @FXML
    private Stage root;

    @FXML
    private Label version;

    @Autowired
    public AboutController(EventBus eventBus, DeeControlContext deeControlContext) {
        super(eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.initStyle(StageStyle.TRANSPARENT);
        root.setAlwaysOnTop(true);

        root.setOnShowing((e) -> {
            setPosition();
            root.requestFocus();
        });

        root.focusedProperty().addListener((ob, o, n) -> {
            if(!n) { root.hide(); }
        });

        root.getScene().setOnMousePressed((e) -> root.hide());

        version.setText(deeControlContext.getVersion());
    }

    private void setPosition() {
        Window owner = root.getOwner();
        if(owner == null) { return; }

        root.setX(owner.getX() + (owner.getWidth() - root.getWidth())/2.0);
        root.setY(owner.getY() + (owner.getHeight() - root.getHeight())/2.0);
    }
}
