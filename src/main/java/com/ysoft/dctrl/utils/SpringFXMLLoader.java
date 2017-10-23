package com.ysoft.dctrl.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javafx.fxml.FXMLLoader;

/**
 * Created by pilar on 21.3.2017.
 */

@Service
public class SpringFXMLLoader {
    private final ApplicationContext applicationContext;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public SpringFXMLLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object load(String url) {
        try (InputStream is = SpringFXMLLoader.class.getResourceAsStream(url)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            return loader.load(is);
        } catch (IOException e) {
            logger.error("FXMLloader error",e);
            throw new RuntimeException(e);
        }
    }
}
