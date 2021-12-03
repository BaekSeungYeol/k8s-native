package com.java.k8snative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync
@ConfigurationPropertiesScan
public class K8sNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sNativeApplication.class, args);
    }


    @RestController
    public static class Test {
        @GetMapping("/test")
        public String test() {
            return "Test success!!";
        }
    }
}
