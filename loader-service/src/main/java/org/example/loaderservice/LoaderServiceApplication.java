package org.example.loaderservice;

import org.example.loaderservice.controller.JiraFetcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class LoaderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoaderServiceApplication.class, args);
//       JiraFetcher.getData();
    }

}
