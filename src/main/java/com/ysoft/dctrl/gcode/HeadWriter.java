package com.ysoft.dctrl.gcode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.Project;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * Created by pilar on 4.7.2017.
 */

@Component
public class HeadWriter {
    private static final int HEAD_SIZE = 1024;
    private final DeeControlContext deeControlContext;
    private final EventBus eventBus;

    private final String gcodePath;

    @Autowired
    public HeadWriter(DeeControlContext deeControlContext, EventBus eventBus, FilePathResource filePathResource) {
        this.deeControlContext = deeControlContext;
        this.eventBus = eventBus;
        this.gcodePath = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
    }

    public void writeHead(String input, String output) {
        final WriteHeadTask writeHeadTask = new WriteHeadTask(input, output, getHead());

        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), (e) -> {
            eventBus.publish(new Event(EventType.GCODE_HEAD_WRITE_PROGRESS.name(), writeHeadTask.getExportProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        writeHeadTask.setOnSucceeded((e) -> {
            eventBus.publish(new Event(EventType.GCODE_HRAD_WRITTEN.name()));
            timeline.stop();
        });

        new Thread(writeHeadTask).start();
    }

    public byte[] getHead() {
        final byte[] head = new byte[HEAD_SIZE];

        Project project = deeControlContext.getCurrentProject();

        writeToBuffer(head, "dgrter", 0, 8);
        writeToBuffer(head, 1, 8, 2);
        writeToBuffer(head, 0, 10, 2);

        Map<String, Long> materials = project.getMaterialUsage();
        List<String> keys = new ArrayList<>(materials.keySet());
        int offset = 12;
        for(int i = 0; i < 16; i++) {
            if(i >= keys.size()) { break; }

            String k = keys.get(i);

            writeToBuffer(head, k.toLowerCase(), offset, 4);
            offset += 4;
            writeToBuffer(head, materials.get(k), offset, 4);
            offset += 4;
        }

        writeToBuffer(head, project.getPrintDuration(), 140, 4);
        writeToBuffer(head, Instant.now().toEpochMilli(), 146, 8);

        File gcode = new File(gcodePath);
        writeToBuffer(head, gcode.length() + HEAD_SIZE, 1016, 4);

        return head;
    }

    private void writeToBuffer(byte[] buffer, long data, int offset, int length) {
        byte[] d = new byte[length];
        for(int i = 0, j = length - 1; i < length; i++, j--) {
            d[j] =(byte) ((data >> 8*i) & 0xff);
        }
        writeToBuffer(buffer, d, offset, length);
    }

    private void writeToBuffer(byte[] buffer, String data, int offset, int length) {
        String padded = String.format("%-" + length + "s", data);
        writeToBuffer(buffer, padded.getBytes(StandardCharsets.UTF_8), offset, length);
    }

    private void writeToBuffer(byte[] buffer, byte[] data, int offset, int length) {
        for(int i = 0; i < length; i++) {
            buffer[offset + i] = data[i];
        }
    }

    private class WriteHeadTask extends Task<Void> {
        private volatile long bytesWritten;
        private volatile long totalBytes;

        private File input;
        private File output;
        private byte[] head;

        public WriteHeadTask(String in, String out, byte[] head) {
            this.input = new File(in);
            this.output = new File(out);
            this.head = head;
        }

        @Override
        protected Void call() throws Exception {
            if(!input.exists()) { throw new IOException("Input file not found"); }

            totalBytes = input.length() + HEAD_SIZE;
            bytesWritten = 0;

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output), 1024*1024);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input), 1024*1024);

            bos.write(head, 0, HEAD_SIZE);
            bytesWritten += HEAD_SIZE;

            byte[] buffer = new byte[1024*1024];
            int read;
            while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                bytesWritten += read;
                bos.write(buffer, 0, read);
            }

            return null;
        }

        public double getExportProgress() {
            return bytesWritten/totalBytes;
        }
    }
}
