package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.scene.control.ProgressBar;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.utils.Project;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
    }

    private void startSlice(String stlPath) {
        System.err.println("path> " + stlPath);
        runner = new SlicerRunner(eventBus, currentSlicer, slicerParams.getAllParams(), stlPath);

        runner.setOnSucceeded((e) -> {
            System.err.println("slice done");
            Project project = deeControlContext.getCurrentProject();
            project.setPrintDuration(runner.getDuration());
            for(Long m : runner.getMaterialUsage()) {
                if(m == null) continue;

                project.addMaterial("PLA", m);
            }
        });

        runner.setOnFailed((e) -> {
            System.err.println("fail");
            runner.getException().printStackTrace();
        });

        new Thread(runner).start();
    }

    public void stopSlice() {
        if(runner != null) runner.cancel();
        runner = null;
    }

    private void setSlicer(String id){
        this.currentSlicer = slicerMap.get(id);
        this.selectedSlicerID = id;
    }
}
