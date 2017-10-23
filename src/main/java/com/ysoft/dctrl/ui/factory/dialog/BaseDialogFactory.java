package com.ysoft.dctrl.ui.factory.dialog;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.ui.control.DialogPane;
import com.ysoft.dctrl.ui.dialog.Dialog;
import com.ysoft.dctrl.utils.SpringFXMLLoader;

import javafx.scene.layout.Pane;

/**
 * Created by pilar on 23.5.2017.
 */

@Component
public class BaseDialogFactory implements
        DialogWrapperFactory,
        PreferencesFactory,
        TextInputFactory
{
    private final SpringFXMLLoader loader;

    public BaseDialogFactory(SpringFXMLLoader loader) {
        this.loader = loader;
    }

    public Dialog createDialog(String view) {
        DialogPane dialog = (DialogPane) loader.load(view);
        return new Dialog(dialog);
    }

    @Override
    public Pane buildWrapper() {
        return (Pane) loader.load("/view/dialog/dialog_wrapper.fxml");
    }

    @Override
    public Dialog buildPreferences() {
        return createDialog("/view/dialog/preferences.fxml");
    }

    @Override
    public Dialog buildTextInput() { return createDialog("/view/dialog/text_input.fxml"); }

}
