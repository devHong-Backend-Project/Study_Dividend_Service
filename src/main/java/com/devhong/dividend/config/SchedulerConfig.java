package com.devhong.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // poolSize는 cpu 처리가 많은 경우 core개수 + 1
        // I/O 처리가 많은 경우 core개수*2
        threadPool.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool);
    }
}
