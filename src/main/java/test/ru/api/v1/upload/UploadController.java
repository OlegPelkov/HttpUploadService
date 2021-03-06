package test.ru.api.v1.upload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import test.ru.channelMaps.FileChannelMap;
import test.ru.channelMaps.FreeSpaceCounter;
import test.ru.channelMaps.RequestChannelMap;
import test.ru.channel.FileDataChannel;
import test.ru.channel.RequestDataChannel;
import test.ru.fileAttributs.FileAttribute;
import test.ru.view.DurationView;
import test.ru.view.FileUploadView;
import test.ru.view.ProgressView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static test.ru.utils.Utils.*;

@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    private static final int MAX_FILE_SIZE = 52428800;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final RequestChannelMap requestChannelMap = RequestChannelMap.getInstance();
    private final FileChannelMap fileChannelMap = FileChannelMap.getInstance();

    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public String uploadPost(@RequestHeader(name = "X-Upload-File", required = true) String fileName,
                             @RequestHeader(name = "Content-Length", required = true) Integer size, HttpServletRequest request) throws IOException {

        LOG.info("POST REQUEST: /v1/upload: filename:{} size:{}", fileName, size);

        int bytesCountReaded = 0;
        int countOfOperations = 0;
        int bytesRead = 1;
        InputStream is;

        try {
            if (size > 0 && size <= MAX_FILE_SIZE) {
                RequestDataChannel requestDataChannel = new RequestDataChannel(new FileAttribute(fileName, getFileNameWithTimeStamp(), size));
                if (requestChannelMap.containsKey(requestDataChannel.getFileAttribute().getFileName())) {
                    return LogPostResponse("/v1/upload:", "File " + fileName + " already loading");
                }
                requestChannelMap.putIfAbsent(requestDataChannel.getFileAttribute().getFileName(), requestDataChannel);
                FileDataChannel currentFileChannel = new FileDataChannel(requestDataChannel.getFileAttribute());
                fileChannelMap.putIfAbsent(currentFileChannel.getFileAttribute().getFileName(), currentFileChannel);
                LOG.info("Start upload process for :{} size:{}", fileName, size);
                if (isExist(fileName) && !deleteOldFile(fileName)) {
                    LOG.error("Can not delete old file {}", fileName);
                }
                is = request.getInputStream();
                byte[] buffer = new byte[size];
                boolean read = true;
                while (read && bytesCountReaded<size) {
                    if ((bytesRead = is.read(buffer)) > 0 && FreeSpaceCounter.getInstance().isHaveFreeSpace()) {
                        byte[] transferBuffer = new byte[bytesRead];
                        System.arraycopy(buffer, 0, transferBuffer, 0, bytesRead);
                        requestDataChannel.add(transferBuffer);
                        countOfOperations++;
                        bytesCountReaded += bytesRead;
                        FreeSpaceCounter.getInstance().add(bytesCountReaded);
                        LOG.debug("Write to requestDataChannel {} bytes {} block {}", fileName, bytesRead, countOfOperations);
                        Thread.sleep(10);
                    } else {
                        LOG.debug("Wait ()");
                        Thread.sleep(200);
                    }
                }
            } else {
                return LogPostResponse("/v1/upload:", "Error load " + fileName + " size of file more then 50mb ");
            }
        } catch (Exception e) {
            LOG.error("Error {}", e);
            return LogPostResponse("/v1/upload:", "Error load " + fileName + " " + e);
        }
        String result = fileName + "  " + bytesCountReaded + "  " + "file is loading" + " blocks: " + countOfOperations;
        return LogPostResponse("/v1/upload:", result);
    }

    @RequestMapping(value = "/v1/upload/progress", method = RequestMethod.GET)
    public String uploadGet() {
        LOG.info("GET REQUEST /v1/upload/progress");
        Set<FileUploadView> requestUploadViews = new LinkedHashSet<>();
        for (Map.Entry<String, RequestDataChannel> entry : requestChannelMap.entrySet()) {
            FileAttribute fileAttr = entry.getValue().getFileAttribute();
            requestUploadViews.add(new FileUploadView(fileAttr.getId(), fileAttr.getSize(), entry.getValue().getCountWrittenBytes()));
        }
        String result = gson.toJson(new ProgressView(requestUploadViews));
        LOG.info("GET RESPONSE /v1/upload/progress: {}", result);
        return LogGetResponse("/v1/upload/progress: ", result);
    }

    @RequestMapping(value = "/v1/upload/duration", method = RequestMethod.GET)
    public String durationGet() {
        LOG.info("GET REQUEST /v1/upload/duration");
        Set<String> durationStrings = new LinkedHashSet<>();
        for (Map.Entry<String, FileDataChannel> entry : fileChannelMap.entrySet()) {
            FileAttribute fileAttr = entry.getValue().getFileAttribute();
            durationStrings.add(getFileUploadDurationString(fileAttr.getId(), entry.getValue().getDuration()));
        }
        String result = new DurationView(durationStrings).getView();
        return LogGetResponse("/v1/upload/duration: ", result);
    }

    private String LogPostResponse(String info, String value) {
        LOG.info("POST RESPONSE {} {}", info, value);
        return value;
    }

    private String LogGetResponse(String info, String value) {
        LOG.info("GET RESPONSE {} {}", info, value);
        return value;
    }
}