package com.ysoft.dctrl.instance;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.files.FileValidator;

import javafx.application.Platform;

public class InstanceMonitor {
    Logger logger = LogManager.getLogger(InstanceMonitor.class);
    private final static int PORT = 58936;
    private Server server;
    private EventBus eventBus;
    private final Queue<String> prebusFileQueue;

    public InstanceMonitor() {
        prebusFileQueue = new LinkedList<>();
        eventBus = null;
    }

    public boolean connectClient(List<String> args) {
        try {
            Client client = new Client(PORT);
            client.connect();

            String file = getFileFromArgs(args);
            if (file != null) {
                client.sendMessage(file);
            }

            client.disconnect();
            logger.info("Other instance running");
            return false;
        } catch (IOException e) {
            logger.info("No other instance running");
        }

        return true;
    }

    public boolean startServer() {
        server = new Server(PORT);
        if(!server.start()) { return false; }
        server.setOnMessage((m) -> {
            Platform.runLater(() ->{
                if(eventBus == null) {
                    prebusFileQueue.add(m);
                } else {
                    eventBus.publish(new Event(EventType.ADD_MODEL.name(), m));
                }
            });
        });

        (new Thread(server)).start();

        return true;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        if(!prebusFileQueue.isEmpty()) {
            prebusFileQueue.forEach((f) -> {
                eventBus.publish(new Event(EventType.ADD_MODEL.name(), f));
            });
        }
    }

    public void destroy() {
        server.stop();
    }

    private String getFileFromArgs(List<String> args) {
        for(String arg : args) {
            if(FileValidator.isModelFileSupported(arg)) {
                return arg;
            }
        }

        return null;
    }
}
