package test.ru.api.v1.upload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import test.ru.channelMaps.FileChannelMap;
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

import static test.ru.utils.Utils.getFileNameWithTimeStamp;
import static test.ru.utils.Utils.getFileUploadDurationString;

@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    private static final int MAX_FILE_SIZE = 52428800;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final RequestChannelMap requestChannelMap = RequestChannelMap.getInstance();
    private final FileChannelMap fileChannelMap = FileChannelMap.getInstance();

    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST)
    public String uploadPost(@RequestHeader(name = "X-Upload-File", required = true) String fileName,
                             @RequestHeader(name = "Content-Length", required = true) Integer size, HttpServletRequest request) {

        LOG.info("POST REQUEST: /v1/upload: filename:{} size:{}", fileName, size);

        int bytesCountReaded = 0;
        int countOfOperations = 0;
        InputStream is;
        try {
            if (size > 0 && size < MAX_FILE_SIZE) {
                RequestDataChannel requestDataChannel = new RequestDataChannel(new FileAttribute(fileName, getFileNameWithTimeStamp(), size));
                if (requestChannelMap.containsKey(requestDataChannel.getFileAttribute().getFileName())) {
                    return "File " + fileName + " already loading";
                }
                requestChannelMap.put(requestDataChannel.getFileAttribute().getFileName(), requestDataChannel);
                is = request.getInputStream();
                int bytesRead;
                byte[] buffer = new byte[size];

                while ((bytesRead = is.read(buffer)) > 0) {
                    byte[] transferBuffer = new byte[bytesRead];
                    System.arraycopy(buffer, 0, transferBuffer, 0, bytesRead);
                    requestDataChannel.addLast(transferBuffer);
                    countOfOperations++;
                    bytesCountReaded += bytesRead;
                    LOG.info("Write to requestDataChannel {} bytes {} block {}", fileName, bytesRead, countOfOperations);
                }

                if (bytesCountReaded != size) {
                    throw new Exception("Error byte count transfer");
                }
            }
        } catch (Exception e) {
            LOG.error("Error {}", e);
        }
        String result = fileName + "  " + bytesCountReaded + "  " + "file is loading" + " blocks: " + countOfOperations;
        LOG.info("POST RESPONSE: {}", result);
        return result;
    }

    @RequestMapping(value = "/v1/upload/progress", method = RequestMethod.GET)
    public String uploadGet() {
        LOG.info("GET REQUEST /v1/upload/progress");
        Set<FileUploadView> fileUploadViews = new LinkedHashSet<>();
        Set<FileUploadView> requestUploadViews = new LinkedHashSet<>();
        for (Map.Entry<String, FileDataChannel> entry : fileChannelMap.entrySet()) {
            FileAttribute fileAttr = entry.getValue().getFileAttribute();
            fileUploadViews.add(new FileUploadView(fileAttr.getId(), fileAttr.getSize(), entry.getValue().getCountWrittenBytes()));
        }
        for (Map.Entry<String, RequestDataChannel> entry : requestChannelMap.entrySet()) {
            FileAttribute fileAttr = entry.getValue().getFileAttribute();
            requestUploadViews.add(new FileUploadView(fileAttr.getId(), fileAttr.getSize(), entry.getValue().getCountWrittenBytes()));
        }
        Set<FileUploadView> resultSet = new LinkedHashSet<>();
        resultSet.addAll(fileUploadViews);
        resultSet.addAll(requestUploadViews);
        String result = gson.toJson(new ProgressView(resultSet));
        LOG.info("GET RESPONSE /v1/upload/progress: {}", result);
        return result;
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
        LOG.info("GET RESPONSE /v1/upload/duration: {}", result);
        return result;
    }
}