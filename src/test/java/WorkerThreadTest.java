import com.isaiahvaris.server.core.ServerThread;
import com.isaiahvaris.server.core.WorkerThread;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.Socket;

public class WorkerThreadTest {

    /*
    Test the reading and writing from and to the client
     */
    @Test
    void runTest() throws IOException, InterruptedException {
        ServerThread testThread = new ServerThread();
        Socket socket = testThread.getServerSocket().accept();
        WorkerThread workerThread = new WorkerThread(socket);

        assertNotNull(socket);
        //test browser request and server response by calling http://localhost:8080/ from the browser
        assertEquals("method: GET, targetPath: /, version: HTTP/1.1, host: localhost:8080", workerThread.getRequest(socket));
        assertEquals("200 OK text/html", workerThread.sendResponse(socket));
        assertTrue(socket.isClosed());
    }
}
