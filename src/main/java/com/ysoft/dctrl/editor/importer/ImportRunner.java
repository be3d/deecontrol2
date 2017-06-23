package com.ysoft.dctrl.editor.importer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import com.ysoft.dctrl.utils.YieldTask;
import javafx.concurrent.Task;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 13.4.2017.
 */
public class ImportRunner extends YieldTask<TriangleMesh, GCodeLayer> {
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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(getUpdateTask(), 0, 250);
        TriangleMesh mesh = null;
        try {
            mesh = modelImporter.load(path);
            //eventBus.publish(new Event(EventType.MODEL_LOADED.name(), mesh));
        } catch (IOException e) {

            System.err.println("fuck");
            e.printStackTrace();
        }
        timer.cancel();
        return mesh;
    }

    private TimerTask getUpdateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                eventBus.publish(new Event(EventType.MODEL_LOAD_PROGRESS.name(), modelImporter.getProgress()));
            }
        };
    }
}
