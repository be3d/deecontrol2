package com.ysoft.dctrl.editor.importer;

import java.io.File;
import java.io.IOException;

import com.ysoft.dctrl.editor.mesh.ExtendedMesh;

import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 28.3.2017.
 */
public interface ModelImporter<R> {
    R load(String path) throws IOException, IllegalArgumentException, RunningOutOfMemoryException;
    R load(File file) throws IOException, IllegalArgumentException, RunningOutOfMemoryException;

    void reset();
    void cancel();
    double getProgress();
}
