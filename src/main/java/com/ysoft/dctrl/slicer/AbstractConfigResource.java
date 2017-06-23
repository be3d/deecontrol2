package com.ysoft.dctrl.slicer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.DeeControlContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuhn on 6/13/2017.
 */
@Component
public abstract class AbstractConfigResource {

    protected final DeeControlContext deeControlContext;
    protected final EventBus eventBus;

    @Autowired
    public AbstractConfigResource(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;

    }

    /**
     * Crawls the folder, and uses objectMapper to parse files into java objects of (type)
     */
    public <T> List<T> loadObjects(String path, Class<T> type, Boolean fromResources){

        List<T> objects = new ArrayList<>();
        List<File> fileObjects;

        if (fromResources){
            fileObjects = this.deeControlContext.getFileService().getResourceFiles(path);
        } else {
            fileObjects = this.deeControlContext.getFileService().getUserFiles(path);
        }

        for (File f : fileObjects){

            if (!f.isFile()){ continue; }

            try {
                objects.add(deeControlContext.getObjectMapper().readValue(f, type));
            } catch (JsonMappingException e){
                System.out.println("Config resource: parsing error." + f.toString() + " " + e.getMessage());
                e.printStackTrace();
            } catch ( IOException e){
                System.out.println("Config resource: definition error." + f.toString() + " " + e.getMessage());
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                System.out.println("Config resource: parsing error. " + f.toString() + " " + e.getMessage());
                e.printStackTrace();
            }
        }

        return objects;
    }
}
