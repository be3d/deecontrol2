package com.ysoft.dctrl;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by kuhn on 10/16/2017.
 */
public class DeeControlPreloader extends Preloader {
    private final Logger logger = LogManager.getLogger(DeeControlPreloader.class);

    private final static String SPLASH_CSS = "/css/splash_screen.css";
    private final static String DEV_VERSION = "devel version";

    private Stage preloaderStage;

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if(info instanceof ProgressNotification){
            if(((ProgressNotification)info).getProgress() >= 1.0){
                preloaderStage.getScene().getWindow().hide();
                logger.trace("Application started.");
            }
        }
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        logger.error("Application couldn't start: {}", info.getCause());
        return super.handleErrorNotification(info);
    }

    @Override
    public void start(Stage stage) throws Exception {
        preloaderStage = stage;

        StackPane root = new StackPane();
        StackPane content = new StackPane();

        Scene scene = new Scene(root, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource(SPLASH_CSS).toExternalForm());

        String version = getClass().getPackage().getImplementationVersion();
        Label versionLabel = new Label(version != null ? version : DEV_VERSION);
        versionLabel.getStyleClass().add("version");

        content.getStyleClass().addAll("content");
        content.getChildren().addAll(versionLabel);
        content.setAlignment(Pos.BOTTOM_RIGHT);

        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.setWidth(406);
        preloaderStage.setHeight(210);
        preloaderStage.centerOnScreen();
        preloaderStage.setScene(scene);

        preloaderStage.show();
        root.getChildren().add(content);
    }
}
