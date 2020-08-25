package com.isaiahvaris.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    //Server socket for accepting requests
    private ServerSocket serverSocket;

    public ServerThread() throws IOException {
        this.serverSocket = new ServerSocket(8080);

    }

    @Override
    public void run() {
        try {
            //Serversocket should keep listening to accept requests while it is open
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();


                LOGGER.info(" Connection accepted:" + socket.getInetAddress());
                /*
                One a request is accepted create a new worker thread and start it. The worker thread class handles the process from there
                 */
                WorkerThread workerThread = new WorkerThread(socket);
                workerThread.start();
            }

        } catch (IOException e) {
            LOGGER.error("Socket setup unsuccessful", e);
        } finally {
            if (serverSocket != null) {
                try {
                    //Close server socket
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
