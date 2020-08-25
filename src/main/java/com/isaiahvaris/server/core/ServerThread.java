package com.isaiahvaris.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    private ServerSocket serverSocket;

    public ServerThread() throws IOException {
        this.serverSocket = new ServerSocket(8080);

    }

//    public ServerSocket getServerSocket() {
//        return serverSocket;
//    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                LOGGER.info(" Connection accepted:" + socket.getInetAddress());
                WorkerThread workerThread = new WorkerThread(socket);
                workerThread.start();
            }

        } catch (IOException e) {
            LOGGER.error("Socket setup unsuccessful", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
