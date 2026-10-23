package com.vyshnavi.keytrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeyTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeyTraceApplication.class, args); // <--- Fixed here
    }

}