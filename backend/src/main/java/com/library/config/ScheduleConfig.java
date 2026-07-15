package com.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 定时任务配置类
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 设置定时任务使用的线程池，避免任务阻塞
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(2));
    }
}