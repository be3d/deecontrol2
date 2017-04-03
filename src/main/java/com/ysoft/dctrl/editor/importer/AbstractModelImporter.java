package com.ysoft.dctrl.editor.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 28.3.2017.
 */
public abstract class AbstractModelImporter implements ModelImporter {
    private volatile long size;
    private volatile long bytesRead;

    public AbstractModelImporter() {
        size = 0;
        bytesRead = 0;
    }

    @Override
    public TriangleMesh load(String path) throws IOException {
        return load(new File(path));
    }

    @Override
    public TriangleMesh load(File file) throws IOException {
        if(!file.exists()) { throw new FileNotFoundException("File does not exist [" + file.getAbsolutePath() + "]"); }
        size = file.length();
        TriangleMesh mesh;
        try (InputStream is = new FileInputStream(file)){
            mesh = load(is);
        }
        return mesh;
    }

    abstract TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException;

    protected void addBytesRead(long bytesRead) {
        this.bytesRead += bytesRead;
    }

    @Override
    public double getProgress() {
        return bytesRead*100/size;
    }
}
