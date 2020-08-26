import com.isaiahvaris.server.core.ServerThread;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

class ServerThreadTest {

    /*
    testing request and response from server using HttpClient
     */
    static ServerThread serverThread;
    @BeforeAll
    static void setUp(){
        try {
            serverThread = new ServerThread(8080);
            serverThread.start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    @Test
    void indexTest(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected  = Files.readString(Paths.get("src/main/resources/SampleHtml.html"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")),
                    () -> Assertions.assertEquals(200, response.statusCode())
            );
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }
    @Test
    void jsonTest(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/json"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected  = Files.readString(Paths.get("src/main/resources/SampleJSON.json"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")),
                    () -> Assertions.assertEquals(200, response.statusCode())
            );
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }
    @Test
    void notFoundTest(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/not"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected  = Files.readString(Paths.get("src/main/resources/Sample404.html"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")),
                    () -> Assertions.assertEquals(404, response.statusCode())
            );
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }
}