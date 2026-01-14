package com.example.egov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.egov", "egovframework"})
public class EgovApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovApplication.class, args);
    }

}
