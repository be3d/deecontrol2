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

    public AbstractModelImporter() {
        size = 0;
        bytesRead = 0;
    }

    @Override
    public R load(String path) throws IOException, RunningOutOfMemoryException, InterruptedException {
        return load(new File(path));
    }

    @Override
    public R load(File file) throws IOException, RunningOutOfMemoryException, InterruptedException {
        if(!file.exists()) { throw new FileNotFoundException("File does not exist [" + file.getAbsolutePath() + "]"); }
        size = file.length();
        R result;
        try (InputStream is = new FileInputStream(file)){
            result = load(is);
        }
        return result;
    }

    public abstract R load(InputStream stream) throws IOException, IllegalArgumentException, RunningOutOfMemoryException, InterruptedException;

    protected void addBytesRead(long bytesRead) {
        this.bytesRead += bytesRead;
    }

    @Override
    public double getProgress() {
        return size != 0 ? bytesRead*100/ (double) size : 0;
    }
}
