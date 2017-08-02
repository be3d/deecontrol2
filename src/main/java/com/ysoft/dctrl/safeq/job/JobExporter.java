package com.ysoft.dctrl.safeq.job;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;
import com.ysoft.dctrl.utils.files.FileUtils;

/**
 * Created by pilar on 1.8.2017.
 */

@Component
public class JobExporter {
    private final Logger logger = LogManager.getLogger(JobExporter.class);

    private final JobCreator jobCreator;
    private final EventBus eventBus;
    private final String jobSource;

    @Autowired
    public JobExporter(JobCreator jobCreator, EventBus eventBus, FilePathResource filePathResource) {
        this.jobCreator = jobCreator;
        this.eventBus = eventBus;
        this.jobSource = filePathResource.getPath(FilePath.SAFEQ_JOB_FILE);
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.JOB_EXPORT.name(), (e) -> exportJob((String) e.getData()));
    }

    public void exportJob(String output) {
        eventBus.subscribeOnce(EventType.JOB_FILE_DONE.name(), (e) -> {
            try {
                FileUtils.copyFile(jobSource, output);
            } catch (IOException e1) {
                logger.error("Unable to copy file", e1);
            }
        });

        jobCreator.createJobFile();
    }
}
