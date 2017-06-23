package com.ysoft.dctrl.editor.importer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

/**
 * Created by pilar on 13.4.2017.
 */
public class ImportRunner extends Task<TriangleMesh> {
    private final ModelImporter modelImporter;
    private final String path;
    private final EventBus eventBus;

    public ImportRunner(EventBus eventBus, ModelImporter modelImporter, String path) {
        this.modelImporter = modelImporter;
        this.path = path;
        this.eventBus = eventBus;
    }

    @Override
    protected TriangleMesh call() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), (e) -> {
            eventBus.publish(new Event(EventType.MODEL_LOAD_PROGRESS.name(), modelImporter.getProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        TriangleMesh mesh = null;
        try {
            mesh = modelImporter.load(path);
        } catch (IOException e) {

            System.err.println("fuck");
            e.printStackTrace();
        }

        timeline.stop();
        return mesh;
    }
}
