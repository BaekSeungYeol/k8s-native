package com.java.k8snative.Secret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @Autowired
    MyProperties myProperties;

    @GetMapping("/hello")
    public String hello() {
        return myProperties.getMessage();
    }
}
