package com.paul.insightservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@SpringBootApplication
public class InsightserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightserviceApplication.class, args);
    }

}
