package com.ts.framework.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 定时业务
 * @author wl
 */
@Component
public class SchedulerManager {
    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    public void schedule(ScheduleWork scheduleWork){
    }

}
