package com.ysoft.dctrl.ui.controller.dialog;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.control.DialogPane;
import com.ysoft.dctrl.ui.dialog.contract.TextInputDialogData;
import com.ysoft.dctrl.ui.factory.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 10/12/2017.
 */
@Controller
public class TextInputController extends DialogController {
    private final Logger logger = LogManager.getLogger(TextInputController.class);

    @FXML private DialogPane root;
    @FXML private Label label;
    @FXML private TextField input;
    @FXML private Label description;
    
    @FXML private Button save;
    @FXML private Button cancel;

    private TextInputDialogData dialogData;

    public TextInputController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(e -> root.close());
        super.initialize(location, resources);
    }

    @Override
    protected DialogType getDialogType() {
        return DialogType.TEXT_INPUT;
    }

    @Override
    protected void onShow(Object data) {
        dialogData = (TextInputDialogData)data;
        
        root.setText(dialogData.getHeader());
        label.setText(dialogData.getLabel());
        description.setText(dialogData.getDescription());
        save.setOnAction((l) -> {
            try {
                dialogData.getConsumer().accept(input.getText());
                root.close();
            } catch (IllegalArgumentException e) {
                // todo
                showError(e.getMessage());
            }
        });
    }

    private void showError(String text){}
}
