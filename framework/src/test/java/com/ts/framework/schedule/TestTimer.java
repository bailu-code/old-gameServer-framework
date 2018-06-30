package com.ts.framework.schedule;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wl
 */
@Component
public class TestTimer {

    @Test
    public void testSpringTimer() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) context.getBean("scheduler");
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("begin");
            }
        }, new Date(System.currentTimeMillis() + 3000));
        TimeUnit.HOURS.sleep(1);
    }

    @Scheduled(initialDelay = 3000, fixedRate = 1000)
    public void corn() throws InterruptedException {
        System.out.println(new Date() +" start");
        TimeUnit.SECONDS.sleep(2);
    }

}
