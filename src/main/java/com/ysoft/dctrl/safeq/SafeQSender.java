package com.ysoft.dctrl.safeq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.safeq.lpr.DefaultLprSender;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.settings.SafeQSettings;

import javafx.concurrent.Task;

/**
 * Created by pilar on 12.4.2017.
 */

@Component
public class SafeQSender {
    private EventBus eventBus;
    private DeeControlContext deeControlContext;

    @Autowired
    public SafeQSender(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
    }

    public void sendJob(String jobPath) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                SafeQSettings safeQSettings = deeControlContext.getSettings().getSafeQSettings();
                String username = System.getProperty("user.name");
                File f;
                try (FileInputStream fis = new FileInputStream(f = new File(jobPath))) {
                    DefaultLprSender sender = new DefaultLprSender(safeQSettings.getSpoolerAddress(), InetAddress.getByName(safeQSettings.getSpoolerAddress()).getHostName(), Integer.valueOf(safeQSettings.getSpoolerPort()));
                    sender.send(username, "YSoft.be3D", deeControlContext.getCurrentProject().getName(), fis, f.length());
                }
                return null;
            }
        };

        task.setOnSucceeded((e) -> {
            eventBus.publish(new Event(EventType.JOB_SEND_DONE.name()));
        });

        task.setOnFailed((e) -> {
            eventBus.publish(new Event(EventType.JOB_SEND_FAILED.name()));
        });

        new Thread(task).start();
    }
}
