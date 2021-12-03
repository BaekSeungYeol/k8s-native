package com.java.k8snative.Secret;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("my")
public class MyProperties {

    private String message;

    public MyProperties(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}

