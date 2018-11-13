package test.ru.api.v1.upload;

import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import test.ru.DataMapBuffer;
import test.ru.TaskContainer;
import test.ru.WriteFileTask;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
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
        try {
            if (size > 0 && size < 50000000) {

                LinkedBlockingDeque blockingQueue = new LinkedBlockingDeque();
                DataMapBuffer.getInstance().put(fileName, blockingQueue);

                int bytesRead;
                byte[] buffer = new byte[size];

                InputStream is = request.getInputStream();
                while ((bytesRead = is.read(buffer)) > 0) {
                    byte[] transferBuffer = new byte[bytesRead];
                    System.arraycopy(buffer,0,transferBuffer,0,bytesRead);
                    blockingQueue.addLast(transferBuffer);
                    bytesCountReaded+=bytesRead;
                    LOG.info("Write to map " + fileName + " " + bytesRead);
                }
                if(bytesCountReaded != size){
                    throw new Exception("Error byte count transfer");
                }
            }
        } catch (Exception e){
            LOG.error("Error {}",e);
        }
        String result = fileName + "  " + bytesCountReaded + "  " + "  "+"success";
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