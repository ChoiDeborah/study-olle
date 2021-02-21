package com.studyolle.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        log.info("processors count {}", processors);
        // 처리할 태스트(이벤트)가 생겼을 때,
        // Active thread가 core pool size 보다 작으면 남아 있는 쓰레드를 사용한다.
        // 현재 일하고 있는 쓰레드 개수가 코어 개수만큼 차 있으면 queue capacity가 찰때까지 큐에 쌓아둔다.
        // 큐 용량이 다 차면, 코어 개수를 넘어서 max pool size에 다르기 전 까지 새로운 쓰레드를 만들어 처리한다.
        // 맥스 개수를 넘기면 태스크를 처리하지 못한다.

        // 현재 시스템의 프로세스 개수 만큼 코어사이즈 설정 (depend on CPU)
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        // depend on Memory
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
