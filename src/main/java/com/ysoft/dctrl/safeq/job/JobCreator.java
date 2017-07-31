package com.ysoft.dctrl.safeq.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.gcode.HeadWriter;
import com.ysoft.dctrl.utils.Project;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by pilar on 26.5.2017.
 */

@Component
public class JobCreator {
    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;
    private final HeadWriter headWriter;

    private volatile long bytesRead;
    private volatile long totalBytes;

    private final String imagePath;
    private final String metadataPath;
    private final String tmpJobPath;
    private final String gcodePath;

    @Autowired
    public JobCreator(EventBus eventBus, DeeControlContext deeControlContext, FilePathResource filePathResource, HeadWriter headWriter) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.headWriter = headWriter;
        this.imagePath = filePathResource.getPath(FilePath.SCENE_IMAGE_FILE);
        this.metadataPath = filePathResource.getPath(FilePath.JOB_META_FILE);
        this.tmpJobPath = filePathResource.getPath(FilePath.SAFEQ_JOB_FILE);
        this.gcodePath = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
        bytesRead = 0;
        totalBytes = 0;
    }

    public void createJobFile() {
        final String jobTitle = deeControlContext.getCurrentProject().getName();

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                createMetaDataFile();
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), (e) -> {
                    eventBus.publish(new Event(EventType.JOB_FILE_PROGRESS.name(), getProgress()));
                }));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();

                File o = new File(tmpJobPath);
                File meta = new File(metadataPath);
                File thumb = new File(imagePath);
                File gcode = new File(gcodePath);
                byte[] head = headWriter.getHead();

                totalBytes = meta.length() + thumb.length() + gcode.length();
                ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(o));
                addZipEntry(zipOutput, jobTitle + ".json", new FileInputStream(meta));
                addZipEntry(zipOutput, jobTitle + ".png", new FileInputStream(thumb));
                addZipEntry(zipOutput, jobTitle + ".gco", new ByteArrayInputStream(head), new FileInputStream(gcode));
                zipOutput.close();

                timeline.stop();

                return tmpJobPath;
            }
        };

        task.setOnSucceeded((e) -> {
            eventBus.publish(new Event(EventType.JOB_FILE_DONE.name(), task.getValue()));
        });

        new Thread(task).start();
    }

    private void addZipEntry(ZipOutputStream zos, String zipEntry, InputStream... iss) throws IOException {
        zos.putNextEntry(new ZipEntry(zipEntry));

        for(InputStream is : iss) {
            byte buffer[] = new byte[16384];
            int len;
            while ((len = is.read(buffer)) > 0) {
                bytesRead += len;
                zos.write(buffer, 0, len);
            }

            is.close();
        }

        zos.closeEntry();
    }

    private void createMetaDataFile() throws IOException {
        Project project = deeControlContext.getCurrentProject();
        MetaData metaData = new MetaData();
        metaData.setPrinterType("DeeGreen");
        metaData.setPrintDurationInMins(SECONDS.toMinutes(project.getPrintDuration()));
        final ArrayList<Material> materials = new ArrayList<>();
        project.getMaterialUsage().forEach((t, l) -> {
            materials.add(new Material(t, l));
        });
        metaData.setMaterial(materials);

        deeControlContext.getObjectMapper().writeValue(new File(metadataPath), metaData);
    }

    private double getProgress() {
        return ((double) bytesRead)/((double) totalBytes);
    }
}
