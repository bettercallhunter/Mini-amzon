package org.mini_amazon.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class TaskConfig {

  @Bean("taskExecutor1")
  public TaskExecutor taskExecutor1() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(100);
    return executor;
  }

//  @Bean("taskExecutor2")
//  public TaskExecutor taskExecutor2() {
//    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(2);
//    executor.setMaxPoolSize(10);
//    return executor;
//  }
}
