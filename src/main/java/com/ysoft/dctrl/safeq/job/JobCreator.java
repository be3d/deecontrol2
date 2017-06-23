package com.ysoft.dctrl.safeq.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * Created by pilar on 26.5.2017.
 */

@Component
public class JobCreator {
    private static final String SNAP_SHOT_FILE_NAME = "image.png";
    private static final String JOB_FILE_NAME = "temp.3djob";

    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;

    private volatile long bytesRead;
    private volatile long totalBytes;

    @Autowired
    public JobCreator(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        bytesRead = 0;
        totalBytes = 0;
    }

    public void creteJobFIle(String jobTitle) {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String tempFolder = deeControlContext.getSlicerTempFolder() + File.separator;
                eventBus.publish(new Event(EventType.TAKE_SCENE_SNAPSHOT.name(), tempFolder + SNAP_SHOT_FILE_NAME));

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), (e) -> {
                    eventBus.publish(new Event(EventType.JOB_FILE_PROGRESS.name(), getProgress()));
                }));

                File o = new File(tempFolder + JOB_FILE_NAME);
                File meta = new File(tempFolder + "metadata.json");
                File thumb = new File(tempFolder + SNAP_SHOT_FILE_NAME);
                File gcode = new File(tempFolder + "sliced.gcode");
                totalBytes = meta.length() + thumb.length() + gcode.length();
                ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(o));
                addZipEntry(zipOutput, jobTitle + ".json", meta);
                addZipEntry(zipOutput, jobTitle + ".png", thumb);
                addZipEntry(zipOutput, jobTitle + ".gco", gcode);
                zipOutput.close();

                return tempFolder + JOB_FILE_NAME;
            }
        };

        task.setOnSucceeded((e) -> {
            eventBus.publish(new Event(EventType.JOB_FILE_DONE.name(), task.getValue()));
        });
    }

    private void addZipEntry(ZipOutputStream zos, String zipEntry, File inputFile) throws IOException {
        zos.putNextEntry(new ZipEntry(zipEntry));
        FileInputStream fis = new FileInputStream(inputFile);

        byte buffer[] = new byte[16384];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            bytesRead += len;
            zos.write(buffer, 0, len);
        }

        fis.close();

        zos.closeEntry();
    }

    private double getProgress() {
        return ((double) bytesRead)/((double) totalBytes);
    }
}
