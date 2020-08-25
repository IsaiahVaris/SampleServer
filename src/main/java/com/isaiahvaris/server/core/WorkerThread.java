package com.isaiahvaris.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            getRequest(socket);
            sendResponse(socket);

            LOGGER.info("Connection Processing Finished.");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }

    private static void getRequest(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder clientRequestBuilder = new StringBuilder();
                String line;
                while (!(line = br.readLine()).isBlank()) {
                    clientRequestBuilder.append(line + "\r\n");
                }

                String[] requestsLines = clientRequestBuilder.toString().split("\r\n");

                String[] startLine = requestsLines[0].split(" ");
                String method = startLine[0];
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
                e.printStackTrace();
            }
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html";
        }
        if ("/json".equals(path)) {
            path = "";
        }
        return Paths.get("/tmp/www", path);
    }



//TODO sendresponse method and getting paths
    private static void sendResponse(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String html;

        // TODO we write
        html = "<html><head><title>Simple Java HTTP ServerThread</title></head><body><h1>This is simple</h1></body></html>";

        final String CRLF = "\n\r"; // 13, 10 (ASCII)

        String response =
                "HHTP/1.1 200 OK" + CRLF + // Status line : HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                        "Content-length: " + html.getBytes().length + CRLF + //HEADER
                        CRLF +
                        html +
                        CRLF + CRLF;

        outputStream.write(response.getBytes());
    }
}
