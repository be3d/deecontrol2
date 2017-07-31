package com.ysoft.dctrl.utils.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Created by pilar on 30.6.2017.
 */

@Component
public class FilePathResource {
    private final ResourcePatternResolver resourcePatternResolver;

    public FilePathResource() {
        ClassLoader cl = this.getClass().getClassLoader();
        resourcePatternResolver = new PathMatchingResourcePatternResolver(cl);
    }

    @PostConstruct
    public void init() {
        for(FilePath fp : FilePath.values()) {
            File f = new File(fp.getPath().getPathString());
            if(fp.getPath().isFolder() && !fp.getPath().isResource() && !f.exists()) {
                if(!f.mkdirs()) {
                    System.err.println("Unable to create folder: " + f.getAbsolutePath());
                }
            }
        }

        System.err.println(System.getProperties().toString());
    }

    public String getPath(FilePath path) {
        return path.getPath().getPathString();
    }

    public Resource[] listResources(FilePath resourcePattern) throws IOException {
        return resourcePatternResolver.getResources(resourcePattern.getPath().getPathString());
    }

    public File[] listFiles(FilePath path) throws IOException {
        File dir = new File(path.getPath().getPathString());
        if(!dir.exists()) { throw new IOException("Directory not found"); }
        return dir.listFiles();
    }

    public static String getLoggerDir() {
        return FilePath.LOGGER_DIR.getPath().getPathString();
    }
}
