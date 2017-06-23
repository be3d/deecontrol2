package com.ysoft.dctrl.safeq.lpr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ysoft.dctrl.editor.SceneGraph;

/**
 * Created by pilar on 12.4.2017.
 */
public class PrintJob {
    private final String userName;
    private final String queue;
    private final String fileName;
    private final InputStream input;
    private final Socket client;
    private InputStream clientInput;
    private OutputStream clientOutput;
    private final long length;

    public PrintJob(String userName, String queue, String fileName, InputStream input, long length, Socket client) {
        this.userName = userName;
        this.queue = queue;
        this.fileName = fileName;
        this.input = input;
        this.client = client;
        this.length = length;
    }

    public String getUserName() {
        return userName;
    }

    public String getQueue() {
        return queue;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInput() {
        return input;
    }

    public InputStream getClientInput() throws IOException {
        if(clientInput == null) { clientInput = client.getInputStream(); }

        return clientInput;
    }

    public OutputStream getClientOutput() throws IOException {
        if(clientOutput == null) { clientOutput = client.getOutputStream(); }

        return clientOutput;
    }

    public long getLength() {
        return length;
    }
}
