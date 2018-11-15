import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import test.ru.Application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ServletWebServerFactoryAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadControllerTest {

    @LocalServerPort
    private int port;


    @Test
    public void testGetProgress() throws JSONException {

        HttpHeaders headers = new HttpHeaders();

        TestRestTemplate restTemplate = new TestRestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/upload/progress"), HttpMethod.GET, entity, String.class);

        String expected = "{uploads:[]}";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testUploadFiles() throws JSONException, IOException {

        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Datafile.dat");
        headers.add("X-Upload-File","DataFile.dat");
        byte[] data = IOUtils.toByteArray(stream);

        HttpEntity<String> entity = new HttpEntity<String>(new String(data), headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/upload"), HttpMethod.POST, entity, String.class);

        String expected = "{uploads:[]}";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}