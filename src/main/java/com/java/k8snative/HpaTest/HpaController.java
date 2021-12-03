package com.java.k8snative.HpaTest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HpaController {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public static boolean flag = true;

    @GetMapping("/cpu")
    public String cpu() {
        double x = 1d;

        log.info("for 60 second ");
        eventPublisher.publishEvent(this);

        while(flag) {
            x += Math.sqrt(x);
            //log.info(String.valueOf(x));
        }
        return "consume finished";
    }}

