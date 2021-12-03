package com.java.k8snative.GracefulCheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class GracefulService {
    private boolean healthy = true;

    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

//    public void unhealthy(ThreadPoolExecutor executor) {
//        setHealthy(false);
//        log.info("executor getActiveCount = " + executor.getActiveCount());
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