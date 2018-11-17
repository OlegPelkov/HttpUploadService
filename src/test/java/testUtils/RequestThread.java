package testUtils;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class RequestThread extends Thread {

    private CountDownLatch latch;
    private HttpEntity<byte[]> entity;
    private int port;
    private Header header;

    public RequestThread(CountDownLatch latch, HttpEntity<byte[]> entity, int port, Header header) {
        this.latch = latch;
        this.entity = entity;
        this.port = port;
        this.header = header;
        start();
    }

    @Override
    public void run() {
        latch.countDown();
        // ResponseEntity<String> response = new TestRestTemplate().exchange(createURLWithPort("/api/v1/upload"), HttpMethod.POST, entity, String.class);
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://localhost:" + port + "/api/v1/upload");
        httppost.setEntity(new ByteArrayEntity(entity.getBody()));
        httppost.setHeader(header);
        //Execute and get the response.
        try {
            HttpResponse response = httpclient.execute(httppost);
            org.apache.http.HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                InputStream instream = null;
                try {
                    instream = responseEntity.getContent();
                } finally {
                    instream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
