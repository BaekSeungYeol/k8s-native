package com.java.k8snative.GracefulCheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@Slf4j
public class GracefulController {


    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    @Autowired
    GracefulService gracefulService;

    @GetMapping("/gtest")
    public String gtest() {
        return "gtest";
    }

    @GetMapping("/work")
    public String work() throws InterruptedException {
        log.info("Wait for 10 seconds");
        executor.execute(this::task);
        log.info("Main work Finished");
        return "Main work finished";
    }

    private void task() {
        try {
            log.info("Working Thread = " + Thread.currentThread());
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Completed 10 seconds.. Thread work Finished");
    }


    @GetMapping("/unhealthy")
    public void unhealth() {
        log.info("executor getActiveCount = " + executor.getActiveCount());

        while (executor.getActiveCount() > 0) {
            try {
                System.out.println("Prestop = " + Thread.currentThread());
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        System.out.println("Completed all active threads");
    }







//    @PreDestroy
//    public void destroy() {
//        log.info("executor getActiveCount = " + executor.getActiveCount());
//
//        while (executor.getActiveCount() > 0) {
//            try {
//                System.out.println("Prestop = " + Thread.currentThread());
//                Thread.sleep(10000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        executor.shutdown();
//        System.out.println("Completed all active threads");
//    }
}
