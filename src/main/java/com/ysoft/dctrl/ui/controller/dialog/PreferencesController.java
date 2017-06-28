package com.ysoft.dctrl.ui.controller.dialog;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.DialogPane;
import com.ysoft.dctrl.ui.control.complex.Picker;
import com.ysoft.dctrl.ui.control.complex.TextInput;
import com.ysoft.dctrl.ui.factory.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.settings.SafeQSettings;
import com.ysoft.dctrl.utils.settings.Settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by pilar on 16.5.2017.
 */

@Controller
public class PreferencesController extends DialogController {
    @FXML private DialogPane root;

    /* GENERAL */
    @FXML public Picker language;

    /* NETWORK */
    @FXML public TextInput flexiSpoolerAddress;
    @FXML public TextInput flexiSpoolerPort;

    @FXML public Button save;
    @FXML public Button cancel;

    public PreferencesController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        save.setOnAction((e) -> apply());
        cancel.setOnAction(e -> root.close());

        super.initialize(location, resources);
    }

    @Override
    protected DialogType getDialogType() {
        return DialogType.PREFERENCES;
    }

    public void apply() {
        Settings settings = deeControlContext.getSettings();
        SafeQSettings safeQSettings = settings.getSafeQSettings();

        safeQSettings.setSpoolerAddress(flexiSpoolerAddress.getText());
        safeQSettings.setSpoolerPort(flexiSpoolerPort.getText());

        Locale oldLocale = settings.getStartUpLocale();
        Locale locale = localeFromString((String) language.getValue());
        settings.setStartUpLocale(locale);

        settings.save();

        if(oldLocale != locale) {
            eventBus.publish(new Event(EventType.CHANGE_LANGUAGE.name(), locale));
        }

        root.close();
    }

    @Override
    protected void onShow(Object data) {
        Settings settings = deeControlContext.getSettings();
        Locale locale = settings.getStartUpLocale();
        language.setSelected(locale.getLanguage() + "_" + locale.getCountry());

        SafeQSettings safeQSettings = settings.getSafeQSettings();
        flexiSpoolerAddress.setText(safeQSettings.getSpoolerAddress());
        flexiSpoolerPort.setText(safeQSettings.getSpoolerPort());
    }

    public Locale localeFromString(String s) {
        String[] parts = s.split("_");
        return new Locale(parts[0], parts[1]);
    }
}