package com.ysoft.dctrl.safeq.lpr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import com.ysoft.dctrl.editor.mesh.OnMeshChange;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by pilar on 12.4.2017.
 */
public class DefaultLprSender implements LprSender {
    private static final int DEFAULT_PORT_NUMBER = 515;
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    private static final int DEFAULT_TIMEOUT = 30;

    private static final String LINE_END = "\n";
    private static final String JOB_SEQUENCE = "000";
    private static final String CONTROL_FILE_NAME = "cfA";
    private static final String DATA_FILE_NAME = "dfA";

    private final String serverIP;
    private final String hostName;
    private final int portNumber;
    private final String charset;
    private final int networkTimeout;

    public DefaultLprSender(String serverIP, String hostName) {
        this(serverIP, hostName, DEFAULT_PORT_NUMBER);
    }

    public DefaultLprSender(String serverIP, String hostName, int portNumber) {
        this(serverIP, hostName, portNumber, DEFAULT_ENCODING);
    }

    public DefaultLprSender(String serverIP, String hostName, int portNumber, String charset) {
        this(serverIP, hostName, portNumber, charset, DEFAULT_TIMEOUT);
    }

    public DefaultLprSender(String serverIP, String hostName, int portNumber, String charset, int networkTimeout) {
        this.serverIP = serverIP;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.charset = charset;
        this.networkTimeout = networkTimeout;
    }

    public void send(String userName, String queue, String fileName, InputStream jobData, long length) throws IOException {
        try (Socket client = new Socket(hostName, portNumber)) {
            client.setSoTimeout((int) SECONDS.toMillis(networkTimeout));

            PrintJob printJob = new PrintJob(userName, queue, fileName, jobData, length, client);
            send(printJob);
        }

    }

    public void send(PrintJob printJob) throws IOException {
        sendQueue(printJob);
        sendControlFile(printJob);
        sendDataFile(printJob);
    }

    public void sendQueue(PrintJob printJob) throws IOException {
        String cmd = formatLprCommand(new String(new char[]{0x02}), printJob.getQueue());
        sendCommand(cmd.getBytes(charset), printJob);
    }

    public void sendControlFile(PrintJob printJob) throws IOException {
        String cfData = getLprControlFile(printJob.getUserName(), hostName, printJob.getFileName());
        String cfInfoCmd = formatLprCommand(new String(new char[]{0x02}), cfData.length() + " " + CONTROL_FILE_NAME + JOB_SEQUENCE + hostName);

        sendCommand(cfInfoCmd.getBytes(charset), printJob, true);
        sendCommand(cfData.getBytes(charset), printJob);
        sendCommand(new byte[] {(byte) 0x0}, printJob, true);
    }

    public void sendDataFile(PrintJob printJob) throws IOException {
        String cmd = formatLprCommand(new String(new char[]{0x03}), printJob.getLength() + " " + DATA_FILE_NAME + JOB_SEQUENCE + hostName);

        sendCommand(cmd.getBytes(charset), printJob, true);
        pipe(printJob.getInput(), printJob.getClientOutput());
        sendCommand(new byte[] {(byte) 0x0}, printJob, true);
    }

    public void sendCommand(byte[] data, PrintJob printJob) throws IOException {
        sendCommand(data, printJob, false);
    }

    public void sendCommand(byte[] data, PrintJob printJob, boolean waitForResponse) throws IOException {
        printJob.getClientOutput().write(data);

        if(!waitForResponse) { return; }

        InputStream is = printJob.getClientInput();
        int res = is.read();
        //TODO response check should be done here
        is.skip(is.available());
    }

    public String formatLprCommand(String cmd, String data) {
        return cmd + data.replace(LINE_END, " ") + LINE_END;
    }

    public boolean checkResponse(byte[] data) {
        return checkResponse(data[0] & 0xff);
    }

    public boolean checkResponse(int val) {
        return val == 0;
    }

    public String getLprControlFile(String userName, String hostName, String jobId) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLprCommand("H", hostName));
        sb.append(formatLprCommand("P", userName));
        sb.append(formatLprCommand("f", DATA_FILE_NAME + JOB_SEQUENCE + hostName));
        sb.append(formatLprCommand("U", DATA_FILE_NAME + JOB_SEQUENCE + hostName));
        sb.append(formatLprCommand("N", jobId));
        return sb.toString();
    }

    public void pipe(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] buffer = new byte[8192];
        while((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }
}
