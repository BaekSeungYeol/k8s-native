package com.java.k8snative.HpaTest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventHandler {


    @Async
    @EventListener
    public void handle(HpaController controller) throws InterruptedException {
        Thread.sleep(60000L);
        log.info("after 60 second ");
        controller.flag = false;
    }
}
