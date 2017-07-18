package com.ysoft.dctrl.utils.files;

/**
 * Created by pilar on 30.6.2017.
 */
public enum FilePath {
    USER_DIR(Path.getFolderPath(Path.USER_DIR)),
    PWD(Path.getFolderPath(Path.PWD)),
    BIN_DIR(Path.getFolderPath(Path.PWD + Path.SEP + "bin")),
    TMP_DIR(Path.getFolderPath(Path.USER_DIR + Path.SEP + "tmp")),
    SLICER_DIR(TMP_DIR.path.extendAsDir("slicer")),
    PROFILE_DIR(SLICER_DIR.path.extendAsDir("profiles")),

    RESOURCE_SLICER_DEF_DIR(Path.getResourceFolderPath("classpath:print/slicer/definitions")),
    RESOURCE_PROFILE_DIR(RESOURCE_SLICER_DEF_DIR.path.extendAsFile("factory_profiles/*.def.json")),
    RESOURCE_PRINTER_DIR(RESOURCE_SLICER_DEF_DIR.path.extendAsFile("printer/*.def.json")),

    SLICER_GCODE_FILE(SLICER_DIR.path.extendAsFile("sliced.gcode")),
    SCENE_EXPORT_FILE(SLICER_DIR.path.extendAsFile("scene.stl")),
    SCENE_IMAGE_FILE(SLICER_DIR.path.extendAsFile("image.png")),

    JOB_META_FILE(SLICER_DIR.path.extendAsFile("metadata.json")),
    SAFEQ_JOB_FILE(SLICER_DIR.path.extendAsFile("tmp.3djob"));

    private Path path;

    FilePath(Path path) {
        this.path = path;
    }

    Path getPath() {
        return path;
    }
}
