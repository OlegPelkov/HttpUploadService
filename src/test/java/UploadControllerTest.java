import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import test.ru.view.ProgressView;
import test.ru.workThreads.TaskHandlerThread;
import testUtils.FileValidator;
import testUtils.RequestThread;
import testUtils.TestFileCreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static test.ru.utils.Utils.DIR_PATH;
import static testUtils.TestUtils.deleteFolder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ServletWebServerFactoryAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(UploadControllerTest.class);

    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson = gsonBuilder.create();

    @LocalServerPort
    private int port;

    @Test
    public void testGetProgress() throws JSONException {
        ResponseEntity<String> response = makeGetProgress();
        String expected = "{uploads:[]}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    /**
     *
     * The test generates the DataFile.dat file with data and runs requests by requestCount value.
     * Test waits for the download to complete across a request /v1/upload/progress.
     * Then checks that all bytes are loaded
     *
     * **/
    @Test
    public void testUploadFiles() throws JSONException, IOException, InterruptedException {
        //given
        long timeTestStart = System.currentTimeMillis();
        int requestCount = 50;
        byte[] data = TestFileCreator.getTestData();

        deleteFolder(new File(DIR_PATH));
        CountDownLatch latch = new CountDownLatch(requestCount);
        List<RequestThread> requestThreadList = new ArrayList<>();

        Map<String, Integer> fileMap = new HashMap<>();
        for (int i = 0; i < requestCount; i++) {
            String fileName = "DataFile" + i + ".dat";
            fileMap.put(fileName,data.length);
            Header header = new BasicHeader("X-Upload-File", fileName);
            requestThreadList.add(new RequestThread(latch, data, port, header));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error("Error {}", e);
            requestThreadList.clear();
        }

        //when
        boolean threadsTerminate = false;
        while(!threadsTerminate) {
            for (RequestThread requestThread : requestThreadList) {
                threadsTerminate = true;
                if (!requestThread.getState().equals(Thread.State.TERMINATED)) {
                    Thread.sleep(10);
                    threadsTerminate = false;
                    continue;
                }
            }
        }
        ProgressView view = null;
        while((view = gson.fromJson(makeGetProgress().getBody(), ProgressView.class)).size()>0){
            Thread.sleep(1000);
            view.size();
        }

        //then
        LOG.info("Files loaded ");
        Thread.sleep(2000);
        FileValidator fileValidator = new FileValidator();
        LOG.info(" ****** Time of testUploadFiles execute : {} ms ******",System.currentTimeMillis()-timeTestStart);
        Assert.assertTrue(fileValidator.compareFiles(fileMap, DIR_PATH));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private ResponseEntity<String> makeGetProgress() {
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/upload/progress"), HttpMethod.GET, entity, String.class);
        return response;
    }

}