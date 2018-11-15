package test.ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()
    {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(9090);
        factory.setContextPath("/api");
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
        return factory;
    }

    @Bean
    public ThreadPool threadPull()
    {
        try {
            ThreadPool.getInstance().start();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadPool.getInstance();
    }

}