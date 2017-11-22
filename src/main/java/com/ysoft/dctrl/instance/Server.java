package com.ysoft.dctrl.instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Server implements Runnable{
    private final Logger logger = LogManager.getLogger(Server.class);
    private ServerSocket serverSocket;
    private int listeningPort;
    private Consumer<String> onMessage;

    public Server(int listeningPort) {
        serverSocket = null;
        onMessage = null;
        this.listeningPort = listeningPort;
    }

    @Override
    public void run() {
        if(serverSocket == null) {
            logger.warn("Server not started");
            return;
        }
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                ClientListener cl = new ClientListener(client);
                cl.setOnMessage(this::onClientMessage);
                (new Thread(cl)).start();
                logger.debug("Client connected");
            } catch (IOException e) {
                logger.error("Server crashed");
            }
        }
    }

    private void onClientMessage(String message) {
        if(onMessage != null) {
            onMessage.accept(message);
        }
    }

    public void setOnMessage(Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    public boolean start() {
        try {
            serverSocket = new ServerSocket(listeningPort, 1, InetAddress.getByName("localhost"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void stop() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("Unable to close server socket");
        }
        Thread.currentThread().interrupt();
    }

    private class ClientListener implements Runnable {
        private final Socket client;
        private Consumer<String> onMessage;

        public ClientListener(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                String line = input.readLine();
                logger.warn("read line {}", line);
                onMessage.accept(line);
            } catch (IOException e) {
                logger.error("Unable to get client input stream", e);
            }
        }

        public void setOnMessage(Consumer<String> onMessage) {
            this.onMessage = onMessage;
        }

        public void stop() {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("Client socket close failed", e);
            }
        }
    }
}
