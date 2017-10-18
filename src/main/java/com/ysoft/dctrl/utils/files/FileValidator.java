package com.ysoft.dctrl.utils.files;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileValidator {
    private final static Set<String> SUPPORTED_MODEL_EXTENSIONS;

    static {
        Set<String> extensions = new HashSet<>();
        extensions.add(".stl");
        SUPPORTED_MODEL_EXTENSIONS = Collections.unmodifiableSet(extensions);
    }

    public static boolean isModelFileSupproted(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");
        return Files.exists(Paths.get(filePath)) && dotIndex > -1 && SUPPORTED_MODEL_EXTENSIONS.contains(fileName.substring(dotIndex).toLowerCase());
    }
}
