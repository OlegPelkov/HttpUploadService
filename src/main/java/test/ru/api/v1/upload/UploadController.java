package test.ru.api.v1.upload;

import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import test.ru.TaskContainer;
import test.ru.WriteFileTask;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

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