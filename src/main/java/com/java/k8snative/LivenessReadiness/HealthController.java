package com.java.k8snative.LivenessReadiness;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final ApplicationEventPublisher eventPublisher;
    private final ApplicationAvailability availability;


    @GetMapping("/block")
    public String Block() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
        return "Blocked";
    }
    @GetMapping("/accept")
    public String accept() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
        return "Accepted";
    }

    @GetMapping("/break")
    public String Break()  {
        AvailabilityChangeEvent.publish(eventPublisher,this, LivenessState.BROKEN);
        return "Broken";
    }

    @GetMapping("/status")
    public String getStatus() {
        return " Application is now " + availability.getLivenessState() + " and " + availability.getReadinessState();
    }

//    @Async
//    @EventListener
//    public void onChangeEvent(AvailabilityChangeEvent<ReadinessState> readiness) throws InterruptedException {
//        System.out.println("changed to  :" + readiness.getState());
//        if(readiness.getState() == ReadinessState.REFUSING_TRAFFIC) {
//            Thread.sleep(10000L);
//            AvailabilityChangeEvent.publish(eventPublisher,this, ReadinessState.ACCEPTING_TRAFFIC);
//        }
//    }
}
