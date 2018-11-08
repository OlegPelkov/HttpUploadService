package test.ru;

import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController
{
    @RequestMapping(value = "/test/{fileName}", method=RequestMethod.GET)
    public String index(@PathVariable String fileName) {
        return "Greetings from Spring Boot! - "+fileName;
    }

}