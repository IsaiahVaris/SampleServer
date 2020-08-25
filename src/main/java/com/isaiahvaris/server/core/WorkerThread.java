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

    private Socket socket; //Socket for communicating with the client
    private static String targetPath; //indicates the type of request from the client

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //Get request from the client
        getRequest(socket);
        //Send a response back to the client based on the request
        sendResponse(socket);
        LOGGER.info("Connection Processing Finished.");
        try {
            //Socket should be closed after a successful operation
            socket.close();
        } catch (IOException e) {
            LOGGER.info("Socket not closed successfully.", e);

        }
    }

    private static void getRequest(Socket socket) {

        try {
            //request is read through a buffered reader that takes the inputstream from the socket as input
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //request is written to a stringbuilder which will be used to get the part(s) of the request we need for the response
            StringBuilder clientRequestBuilder = new StringBuilder();
            String line;
            while (!(line = br.readLine()).isBlank()) {
                clientRequestBuilder.append(line + "\r\n");
            }

            //split each line of request into an array
            String[] requestsLines = clientRequestBuilder.toString().split("\r\n");

            /*
            According to http protocol the first line of the request consists of
            the method, the target path and the http version in that other
             */
            String[] startLine = requestsLines[0].split(" ");
            String method = startLine[0];
            //target path specified in request determines response from the server
            targetPath = startLine[1];
            String version = startLine[2];
            //the next line of the request contains the host
            String host = requestsLines[1].split(" ")[1];

            //the remaining lines contain headers with various information
            List<String> headers = new ArrayList<>();

            for (int h = 2; h < requestsLines.length; h++) {
                String header = requestsLines[h];
                headers.add(header);
            }

            String requestLog = String.format("Client %s, method %s, targetPath %s, version %s, host %s, headers %s",
                    socket.toString(), method, targetPath, version, host, headers.toString());
            System.out.println(requestLog);

        }  catch (IOException e) {//Catch any IO exception when reading the browser request
            LOGGER.error("Problem with request", e);
        }
    }

    private static void sendResponse(Socket socket) {

        //outputstream to send server response
        try(OutputStream outputStream = socket.getOutputStream()) {
            //We read response from html or json object depending on path gotten from request
            String path = targetPath.equals("/") ? "src/main/resources/SampleHtml.html" :
                    "src/main/resources/SampleJSON.json";

            String contentType = targetPath.equals("/") ? "text/html" : "application/json";

            //read content of file as bytes
            String content = new String(Files.readAllBytes(Path.of(path)));

            //response tailored according to protocol to enable the browser recognise the type of content being sent
            String response = "HTTP/1.1 200 OK" + "\r\n" + "content-type: " + contentType + "\r\n" +
                    "content-length: " + content.length() + "\r\n" + "\r\n" +
                    content +
                    "\r\n" + "\r\n";

            //write the server response
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {//Catch any IO exception when reading from file or writing to our outputstream
            LOGGER.error("Problem with response", e);
        }
    }
}