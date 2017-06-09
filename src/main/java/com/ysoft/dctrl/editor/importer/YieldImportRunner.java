package com.ysoft.dctrl.editor.importer;

import java.io.IOException;

import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.YieldTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

/**
 * Created by pilar on 8.6.2017.
 */
public class YieldImportRunner<T> extends YieldTask<TriangleMesh, T> {
    private EventBus eventBus;
    private YieldModelImporter<T> importer;
    private String path;

    public YieldImportRunner(EventBus eventBus, YieldModelImporter<T> importer, String path) {
        this.eventBus = eventBus;
        this.importer = importer;
        this.path = path;
    }

    @Override
    protected TriangleMesh call() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), (e) -> {
            eventBus.publish(new Event(EventType.MODEL_LOAD_PROGRESS.name(), importer.getProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        TriangleMesh mesh = null;
        try {
            importer.setOnYield(this::yield);
            mesh = importer.load(path);
        } catch (IOException e) {

            System.err.println("fuck");
            e.printStackTrace();
        }

        timeline.stop();
        return mesh;
    }
}