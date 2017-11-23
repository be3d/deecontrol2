package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParam;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kuhn on 5/29/2017.
 */
public class SlicerRunner extends Task<Slicer> {
    private final Logger logger = LogManager.getLogger(SlicerRunner.class);
    private final Slicer slicer;
    private final EventBus eventBus;
    private final String scene; // stl file path
    private final Map<String, SlicerParam> slicerParams;

    public SlicerRunner(EventBus eventBus, Slicer slicer, Map<String, SlicerParam> slicerParams,
                        String scene) {
        this.slicer = slicer;
        this.eventBus = eventBus;
        this.slicerParams = slicerParams;
        this.scene = scene;
    }

    @Override
    protected Slicer call() throws Exception {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), (e) -> {
            eventBus.publish(new Event(EventType.SLICER_PROGRESS.name(), slicer.getProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        try {
            slicer.run(slicerParams, scene);
        } catch (IOException e) {
            logger.warn(e);
            eventBus.publish(new Event(EventType.SLICER_FAILED.name()));
        } finally {
            timeline.stop();
        }

        return null;
    }

    public long getDuration() {
        return slicer.getDuration();
    }

    public Long[] getMaterialUsage() {
        return slicer.getMaterialUsage();
    }

    public int getLayerCount() { return slicer.getLayerCount(); }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        slicer.cancel();
        return super.cancel(mayInterruptIfRunning);
    }
}