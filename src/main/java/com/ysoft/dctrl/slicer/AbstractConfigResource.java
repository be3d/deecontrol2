package com.ysoft.dctrl.slicer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuhn on 6/13/2017.
 */

public abstract class AbstractConfigResource {
    protected final DeeControlContext deeControlContext;
    protected final EventBus eventBus;
    private final FilePathResource filePathResource;

    public AbstractConfigResource(EventBus eventBus, DeeControlContext deeControlContext, FilePathResource filePathResource) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.filePathResource = filePathResource;
    }

    public <T> List<T> loadFromResource(FilePath resourcePath, Class<T> type) throws IOException {
        Resource[] resources = filePathResource.listResources(resourcePath);
        List<T> res = new ArrayList<>();

        for(Resource r : resources) {
            try (InputStream is = r.getInputStream()) {
                res.add(deeControlContext.getObjectMapper().readValue(is, type));
            }
        }

        return res;
    }

    public <T> List<T> loadFromFolder(FilePath folderPath, Class<T> type) throws IOException {
        File[] files = filePathResource.listFiles(folderPath);
        List<T> res = new ArrayList<>();

        for(File f : files) {
            try(InputStream is = new FileInputStream(f)) {
                res.add(deeControlContext.getObjectMapper().readValue(is, type));
            }
        }

        return res;
    }

}
