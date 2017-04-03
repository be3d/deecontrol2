package com.ysoft.dctrl.editor.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 28.3.2017.
 */
public interface ModelImporter {
    TriangleMesh load(String path) throws IOException, IllegalArgumentException;
    TriangleMesh load(File file) throws IOException, IllegalArgumentException;

    double getProgress();
}
