package com.ysoft.dctrl.ui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
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
        ResourceBundle.Control utf8Control = new UTF8Control();
        SUPPORTED_LOCALES.forEach((locale -> {
            bundles.put(locale, ResourceBundle.getBundle("i18n/" + L10N_BUNDLE, locale, utf8Control));
        }));
    }

    private ResourceBundle getBundle(Locale locale) {
        return bundles.containsKey(locale) ? bundles.get(locale) : bundles.get(DEFAULT_LOCALE);
    }

    public String getMessage(Locale locale, String key) {
        return getBundle(locale).getString(key);
    }

    private class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws
                IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;
            if (format.equals("java.properties")) {

                final String resourceName = bundleName.contains("://") ? null : toResourceName(bundleName, "properties");
                if (resourceName == null) {
                    return bundle;
                }
                final ClassLoader classLoader = loader;
                final boolean reloadFlag = reload;
                InputStream stream = null;
                try {
                    stream = AccessController.doPrivileged(
                            new PrivilegedExceptionAction<InputStream>() {
                                public InputStream run() throws IOException {
                                    InputStream is = null;
                                    if (reloadFlag) {
                                        URL url = classLoader.getResource(resourceName);
                                        if (url != null) {
                                            URLConnection connection = url.openConnection();
                                            if (connection != null) {
                                                // Disable caches to get fresh data for
                                                // reloading.
                                                connection.setUseCaches(false);
                                                is = connection.getInputStream();
                                            }
                                        }
                                    } else {
                                        is = classLoader.getResourceAsStream(resourceName);
                                    }
                                    return is;
                                }
                            });
                } catch (PrivilegedActionException e) {
                    throw (IOException) e.getException();
                }
                if (stream != null) {
                    try {
                        bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } finally {
                        stream.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            return bundle;
        }
    }
}
