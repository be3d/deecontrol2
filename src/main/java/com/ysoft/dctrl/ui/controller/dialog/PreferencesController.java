package com.ysoft.dctrl.ui.controller.dialog;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.control.DialogPane;
import com.ysoft.dctrl.ui.control.complex.Picker;
import com.ysoft.dctrl.ui.control.complex.TextInput;
import com.ysoft.dctrl.ui.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.settings.SafeQSettings;
import com.ysoft.dctrl.utils.settings.Settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;

/**
 * Created by pilar on 16.5.2017.
 */

@Controller
public class PreferencesController extends DialogController {
    @FXML private DialogPane root;

    @FXML private TabPane tabPane;

    /* GENERAL */
    @FXML private Picker language;

    /* NETWORK */
    @FXML private TextInput safeQAddress;
    @FXML private TextInput safeQPort;

    @FXML private Button save;
    @FXML private Button cancel;

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
        String address = safeQAddress.getText();
        String port = safeQPort.getText();

        if(isOneEmptyOrNull(address, port) || !isPortValid(port)) {
            tabPane.getSelectionModel().select(1);
            if(StringUtils.isEmpty(address)) {
                safeQAddress.setErrorText(getMessage("preferences_network_safeq_address_missing"));
                safeQAddress.requestFocus();
            } else {
                safeQPort.setErrorText(getMessage("preferences_network_safeq_port_missing"));
                safeQPort.requestFocus();
            }
            return;
        }

        Settings settings = deeControlContext.getSettings();
        SafeQSettings safeQSettings = settings.getSafeQSettings();

        safeQSettings.setSpoolerAddress(safeQAddress.getText());
        safeQSettings.setSpoolerPort(safeQPort.getText());

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
        safeQAddress.setText(safeQSettings.getSpoolerAddress());
        safeQPort.setText(safeQSettings.getSpoolerPort());

        int tabIndex;
        PreferencesTab tab = data == null ? PreferencesTab.GENERAL : (PreferencesTab) data;
        switch (tab) {
            case GENERAL:
                tabIndex = 0;
                break;
            case NETWORK:
                tabIndex = 1;
                break;
            case PROFILES:
                tabIndex = 2;
                break;
            default:
                tabIndex = 0;
                break;
        }
        tabPane.getSelectionModel().select(tabIndex);
    }

    public Locale localeFromString(String s) {
        String[] parts = s.split("_");
        return new Locale(parts[0], parts[1]);
    }

    private boolean isOneEmptyOrNull(String a, String b) {
        return (StringUtils.isEmpty(a) && !StringUtils.isEmpty(b)) || (StringUtils.isEmpty(b) && !StringUtils.isEmpty(a));
    }

    private boolean isPortValid(String port) {
        return !StringUtils.isEmpty(port) && port.length() < 6 && port.matches("^[0-9]+$") && Integer.parseInt(port) < 65536;
    }
}
