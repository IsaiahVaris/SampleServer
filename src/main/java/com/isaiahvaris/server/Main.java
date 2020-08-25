package com.isaiahvaris.server;

import com.isaiahvaris.server.core.ServerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting...");
        try {
            //
            ServerThread serverThread = new ServerThread();
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
