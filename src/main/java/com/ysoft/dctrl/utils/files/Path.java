package com.ysoft.dctrl.utils.files;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ysoft.dctrl.utils.OSVersion;

/**
 * Created by pilar on 30.6.2017.
 */
class Path {
    static final String SEP = File.separator;
    static final String USER_DIR = System.getProperty("user.home") + SEP + ".dctrl";
    static final String PWD = OSVersion.is(OSVersion.MAC) ? System.getProperty("java.library.path").replace("/MacOS", "") : System.getProperty("user.dir");

    private String path;
    private PathType pathType;

    private Path() {}

    private Path(String path, PathType pathType) {
        this.path = path;
        this.pathType = pathType;
    }

    static Path getFolderPath(String path) {
        return new Path(path, PathType.FOLDER);
    }

    static Path getFilePath(String path) {
        return new Path(path, PathType.FILE);
    }

    static Path getResourceFolderPath(String path) {
        return new Path(path, PathType.RESOURCE_FOLDER);
    }

    static Path getResourceFilePath(String path) {
        return new Path(path, PathType.RESOURCE);
    }

    Path extendAsDir(String extension) {
        if(!isFolder()) throw new IllegalArgumentException("Unable to extend file path");

        Function<String, Path> method = pathType == PathType.FOLDER ? Path::getFolderPath : Path::getResourceFolderPath;
        return method.apply(path + SEP + extension);
    }

    Path extendAsFile(String fileName) {
        if(!isFolder()) throw new IllegalArgumentException("Unable to extend file path");

        Function<String, Path> method = pathType == PathType.FOLDER ? Path::getFilePath : Path::getResourceFilePath;
        return method.apply(path + SEP + fileName);
    }

    String getPathString() {
        return path;
    }

    boolean isFolder() { return (pathType == PathType.FOLDER || pathType == PathType.RESOURCE_FOLDER); }

    boolean isResource() { return (pathType == PathType.RESOURCE_FOLDER || pathType == PathType.RESOURCE); }

    enum PathType {
        FILE, FOLDER, RESOURCE, RESOURCE_FOLDER
    }
}
