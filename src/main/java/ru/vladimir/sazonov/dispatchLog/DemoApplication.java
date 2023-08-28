package ru.vladimir.sazonov.dispatchLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.vladimir.sazonov.dispatchLog.format.DateFormatter;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    DAOService daoService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Configuration
    static class MyConfig implements WebMvcConfigurer {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addFormatter(new DateFormatter());
        }
    }
}
