package com.isaiahvaris.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WorkerThread extends Thread {
    public static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private static String targetPath;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        getRequest(socket);
        sendResponse(socket);
        LOGGER.info("Connection Processing Finished.");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//
//
//            LOGGER.info("Connection Processing Finished.");
//        } catch (IOException e) {
//            LOGGER.error("Problem with communication", e);
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {}
//            }
//        }
    }

    private static void getRequest(Socket socket) {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            StringBuilder clientRequestBuilder = new StringBuilder();
            String line;
            while (!(line = br.readLine()).isBlank()) {
                clientRequestBuilder.append(line + "\r\n");
            }

            String[] requestsLines = clientRequestBuilder.toString().split("\r\n");

            String[] startLine = requestsLines[0].split(" ");
            String method = startLine[0];
            //target path specified in request determines response from the server
            targetPath = startLine[1];
            String version = startLine[2];
            String host = requestsLines[1].split(" ")[1];

            List<String> headers = new ArrayList<>();
            for (int h = 2; h < requestsLines.length; h++) {
                String header = requestsLines[h];
                headers.add(header);
            }

            String requestLog = String.format("Client %s, method %s, targetPath %s, version %s, host %s, headers %s",
                    socket.toString(), method, targetPath, version, host, headers.toString());
            System.out.println(requestLog);
        }  catch (IOException e) {
            LOGGER.error("Problem with request", e);
        }
    }

    private static void sendResponse(Socket socket) {
        try(OutputStream outputStream = socket.getOutputStream()) {
            String path = targetPath.equals("/") ? "src/main/resources/SampleHtml.html" :
                    "src/main/resources/SampleJSON.json";
            String contentType = targetPath.equals("/") ? "text/html" : "application/json";

            byte[] content = Files.readAllBytes(Path.of(path));

            String response = "HTTP/1.1 200 OK" + "\r\n" + "Content-Type: " + contentType +
                    "Content-length: " + content.length + "\r\n" + "\r\n" +
                    content +
                    "\r\n" + "\r\n";
            System.out.println(response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Problem with response", e);
        }
    }
}
