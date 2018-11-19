package testUtils;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class RequestThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(RequestThread.class);

    private CountDownLatch latch;
    private byte[] body;
    private int port;
    private Header header;

    public RequestThread(CountDownLatch latch, byte[] body, int port, Header header) {
        this.latch = latch;
        this.body = body;
        this.port = port;
        this.header = header;
        start();
    }

    @Override
    public void run() {
        latch.countDown();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setValidateAfterInactivity(25000000);
        HttpClient httpclient = HttpClientBuilder.create().setConnectionManager(connectionManager).setRetryHandler(new CustomHttpRequestRetryHandler()).build();
        HttpPost httppost = new HttpPost("http://localhost:" + port + "/api/v1/upload");
        httppost.setEntity(new ByteArrayEntity(body));
        httppost.setHeader(header);
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
            LOG.error("Error {}",e);
        }

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private class CustomHttpRequestRetryHandler implements HttpRequestRetryHandler {
        @Override
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            if (executionCount > 10) {
                LOG.warn("Maximum tries reached for client http pool ");
                return false;
            }
            if (exception instanceof org.apache.http.NoHttpResponseException) {
                LOG.warn("No response from server on " + executionCount + " call");
                return true;
            }
            return false;
        }
    }

}
