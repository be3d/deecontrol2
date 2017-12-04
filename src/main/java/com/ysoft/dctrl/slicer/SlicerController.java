package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.Project;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Created by kuhn on 4/25/2017.
 *
 * Basic controller for slicer.
 * Loading, changing etc.
 *
 */
@Component
public class SlicerController {
    private final Logger logger = LogManager.getLogger(SlicerController.class);

    private final SlicerParams slicerParams;
    private final Map<String, Slicer> slicerMap;

    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;

    public String selectedSlicerID;
    public final String sceneSTL;
    private Slicer currentSlicer;

    private SlicerRunner runner;

    @Autowired
    public SlicerController(EventBus eventBus, DeeControlContext deeControlContext, SlicerParams slicerParams, Map<String, Slicer> slicerMap, FilePathResource filePathResource) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.slicerParams = slicerParams;
        this.slicerMap = slicerMap;
        this.setSlicer("Cura");
        this.runner = null;
        this.sceneSTL = filePathResource.getPath(FilePath.SCENE_EXPORT_FILE);
    }
    @PostConstruct
    private void initialize() {
        eventBus.subscribe(EventType.SCENE_EXPORTED.name(), (e) -> startSlice((String) e.getData()));
        eventBus.subscribe(EventType.SLICER_CANCEL.name(), (e) -> {
            if(runner != null) { runner.cancel(); }
        });
    }

    private void startSlice(String stlPath) {
        runner = new SlicerRunner(eventBus, currentSlicer, slicerParams.getAllParams(), stlPath);

        runner.setOnSucceeded((e) -> {
            logger.trace("Slicing succeeded");

            Project project = deeControlContext.getCurrentProject();
            for(Long m : runner.getMaterialUsage()) {
                if(m == null) continue;

                project.addMaterial("PLA", m);
            }

            // Adding 20% extra print duration to mitigate estimation error
            project.setPrintDuration(Math.round(runner.getDuration()*1.2));

            eventBus.publish(new Event(EventType.SLICER_FINISHED.name()));
        });

        runner.setOnFailed((e) -> {
            logger.error("Slicing failed", runner.getException());
            eventBus.publish(new Event(EventType.SLICER_FAILED.name()));
        });

        runner.setOnCancelled((e) -> {
            logger.trace("Slicing cancelled");
            eventBus.publish(new Event(EventType.SLICER_CANCELLED.name()));
        });

        new Thread(runner).start();
    }

    private void setSlicer(String id){
        currentSlicer = slicerMap.get(id);
        selectedSlicerID = id;
    }
}
