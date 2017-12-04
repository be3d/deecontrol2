package com.ysoft.dctrl.ui.factory.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.SpringFXMLLoader;

import javafx.stage.Stage;

@Component
public class BaseComponentFactory implements AboutFactory {

    private final SpringFXMLLoader springFXMLLoader;

    @Autowired
    public BaseComponentFactory(SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
    }

    @Override
    public Stage buildAbout() {
        return (Stage) springFXMLLoader.load("/view/component/about.fxml");
    }
}
