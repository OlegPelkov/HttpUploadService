package test.ru.api.v1.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import test.ru.RequestChannelMap;
import test.ru.channel.RequestDataChannel;
import test.ru.channel.DataFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    private static final int MAX_FILE_SIZE = 52428800;
/**
    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST)
    public String upload(@RequestHeader(name = "X-Upload-File", required = true) String fileName,
                         @RequestHeader(name = "Content-Length", required = true) Integer size, HttpServletRequest request){
            LOG.info("POST: /v1/upload: filename:{} size:{}",fileName,size);
            try {
                if (size > 0 && size < 50000000) {
                    WriteFileTask writeFileTask = new WriteFileTask(request.getInputStream(), size, getFileNameWithTimeStamp(fileName));
                    TaskContainer.getInstance().add(writeFileTask);
                }
            } catch (Exception e){
                LOG.error("Error {}",e);
            }
        String result = fileName + "  " + size + "  " + "  "+"success";
        LOG.info(result);
        return result;
    }
*/

    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST)
    public String upload(@RequestHeader(name = "X-Upload-File", required = true) String fileName,
                         @RequestHeader(name = "Content-Length", required = true) Integer size, HttpServletRequest request){
        LOG.info("POST: /v1/upload: filename:{} size:{}",fileName,size);
        int bytesCountReaded=0;
        int countOfOperations = 0;
        InputStream is;
        try {
            if (size > 0 && size < MAX_FILE_SIZE) {

                RequestDataChannel requestDataChannel = new RequestDataChannel(new DataFile(fileName, size));
                RequestChannelMap.getInstance().put(requestDataChannel.getFile().getFileName(), requestDataChannel);
                is = request.getInputStream();

                int bytesRead;
                byte[] buffer = new byte[size];

                while ((bytesRead = is.read(buffer)) > 0) {
                    byte[] transferBuffer = new byte[bytesRead];
                    System.arraycopy(buffer,0,transferBuffer,0,bytesRead);
                    requestDataChannel.addLast(transferBuffer);
                    countOfOperations++;
                    bytesCountReaded+=bytesRead;
                    LOG.info("Write to requestDataChannel {} bytes {} block {}",fileName,bytesRead, countOfOperations);
                }
                if(bytesCountReaded != size){
                    throw new Exception("Error byte count transfer");
                }
            }
        } catch (Exception e){
            LOG.error("Error {}",e);
        }
        String result = fileName + "  " + bytesCountReaded + "  "+"file is loading"+ " blocks: "+countOfOperations;
        LOG.info(result);
        return result;
    }


    @RequestMapping(value = "/v1/upload", method = RequestMethod.GET)
    public String uploadGet(){
        return "GET!!";
    }

    private String getFileNameWithTimeStamp(String fileName){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy M dd HH-mm-ss");
        Date date = new Date();
        return fileName + "_" + dateFormat.format(date).replace(" ","_");
    }

}