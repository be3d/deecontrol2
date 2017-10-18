package com.ysoft.dctrl.instance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private final Logger logger = LogManager.getLogger(Client.class);
    private int connectionPort;
    private Socket clientSocket;

    public Client(int connectionPort) {
        this.connectionPort = connectionPort;
    }

    public void connect() throws IOException {
        try {
            clientSocket = new Socket(InetAddress.getByName("localhost"), connectionPort);
        } catch (IOException e) {
            logger.error("Unable to connect to server", e);
            throw e;
        }
    }

    public void disconnect() {
        if(clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.warn("Closing client socket failed", e);
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        if(clientSocket == null) {
            logger.error("Client socket not initialised");
            return;
        }

        message += "\n";

        clientSocket.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
        clientSocket.getOutputStream().flush();

    }
}
