package com.ysoft.dctrl.ui.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by pilar on 30.3.2017.
 */

@Component
class LocalizationResource {
    private static final String L10N_BUNDLE = "messages";
    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static final List<Locale> SUPPORTED_LOCALES;
    static {
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.US);
        locales.add(new Locale("cs", "CZ"));
        SUPPORTED_LOCALES = Collections.unmodifiableList(locales);
    }

    private Map<Locale, ResourceBundle> bundles;

    @Autowired
    public LocalizationResource() {
        bundles = new HashMap<>();
    }

    @PostConstruct
    private void loadBoundless() {
        SUPPORTED_LOCALES.forEach((locale -> {
            bundles.put(locale, ResourceBundle.getBundle("i18n/" + L10N_BUNDLE, locale));
        }));
    }

    private ResourceBundle getBundle(Locale locale) {
        return bundles.containsKey(locale) ? bundles.get(locale) : bundles.get(DEFAULT_LOCALE);
    }

    public String getMessage(Locale locale, String key) {
        return getBundle(locale).getString(key);
    }
}
