package com.ysoft.dctrl.gcode;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

/**
 * Created by pilar on 1.8.2017.
 */

@Component
public class GCodeExporter {
    private final EventBus eventBus;
    private final HeadWriter headWriter;

    private final String sourceFile;

    @Autowired
    public GCodeExporter(EventBus eventBus, FilePathResource filePathResource, HeadWriter headWriter) {
        this.eventBus = eventBus;
        this.headWriter = headWriter;
        this.sourceFile = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.GCODE_EXPORT.name(), (e) -> exportSlicedGcode((String) e.getData()));
    }

    public void exportSlicedGcode(String output) {
        headWriter.writeHead(sourceFile, output);
    }
}
