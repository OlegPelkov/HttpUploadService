package test.ru.api.v1.upload;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
public class UploadController
{
    @RequestMapping(value = "/v1/upload", method = RequestMethod.POST)
    public String upload(
            @RequestHeader(name = "X-Upload-File", required = true) String fileName,
            @RequestHeader(name = "Content-Length", required = true) Integer size,
            HttpServletRequest request){
        try {
            InputStream inputStream = request.getInputStream();
            byte[] buffer = new byte[size];
            int bytesRead;
            System.out.println("POST - "+"/v1/upload "+fileName + " "+size);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("").getAbsolutePath()+File.separator+fileName));
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
                System.out.println("POST - " + "write to " + fileName + " " + bytesRead);
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
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