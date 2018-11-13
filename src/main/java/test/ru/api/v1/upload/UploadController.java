package test.ru.api.v1.upload;

import org.springframework.web.bind.annotation.*;
import test.ru.TaskContainer;
import test.ru.WriteFileTask;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UploadController
{
    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST)
    public String upload(
            @RequestHeader(name = "X-Upload-File", required = true) String fileName,
            @RequestHeader(name = "Content-Length", required = true) Integer size,
            HttpServletRequest request){
            System.out.println("POST - "+"/v1/upload "+fileName + " "+size);
            try {
                if (size > 0 && size < 50000000) {
                    WriteFileTask writeFileTask = new WriteFileTask(request.getInputStream(), size, fileName);
                    TaskContainer.getInstance().add(writeFileTask);
                }
            } catch (Exception e){
                System.err.println("--- "+e);
            }


        String result = fileName + "  " + size + "  " + "  "+"success";
        System.out.println("--- "+result);
        return result;
    }

    @RequestMapping(value = "/v1/upload", method = RequestMethod.GET)
    public String uploadGet(){
        return "GET!!";
    }

}