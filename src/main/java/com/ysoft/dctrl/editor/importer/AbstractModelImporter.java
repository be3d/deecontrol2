package com.ysoft.dctrl.editor.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 28.3.2017.
 */
public abstract class AbstractModelImporter<R> implements ModelImporter<R> {
    private volatile long size;
    private volatile long bytesRead;
    private volatile boolean cancelled;

    public AbstractModelImporter() {
        size = 0;
        bytesRead = 0;
        cancelled = false;
    }

    @Override
    public R load(String path) throws IOException, RunningOutOfMemoryException {
        return load(new File(path));
    }

    @Override
    public R load(File file) throws IOException, RunningOutOfMemoryException {
        if(!file.exists()) { throw new FileNotFoundException("File does not exist [" + file.getAbsolutePath() + "]"); }
        size = file.length();
        R result;
        try (InputStream is = new FileInputStream(file)){
            result = load(is);
        }
        return result;
    }

    public abstract R load(InputStream stream) throws IOException, IllegalArgumentException, RunningOutOfMemoryException;

    protected void addBytesRead(long bytesRead) {
        this.bytesRead += bytesRead;
    }

    @Override
    public double getProgress() {
        return size != 0 ? bytesRead*100/size : 0;
    }

    @Override
    public void cancel() { cancelled = true; }

    public boolean isCancelled() { return cancelled; }
}
