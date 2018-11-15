package testUtils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;

public class RequestThread extends Thread {

    private CountDownLatch latch;
    private HttpEntity<byte[]> entity;
    private int port;

    public RequestThread(CountDownLatch latch,  HttpEntity<byte[]> entity, int port) {
        this.latch = latch;
        this.entity = entity;
        this.port = port;
        start();
    }

    @Override
    public void run() {
        latch.countDown();
        ResponseEntity<String> response = new TestRestTemplate().exchange(createURLWithPort("/api/v1/upload"), HttpMethod.POST, entity, String.class);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
