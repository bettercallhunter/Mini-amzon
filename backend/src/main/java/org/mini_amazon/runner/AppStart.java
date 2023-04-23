package org.mini_amazon.runner;

import org.mini_amazon.socket_servers.AmazonDaemon;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class AppStart implements ApplicationRunner {
  @Resource
  TaskExecutor taskExecutor1;
  //  @Resource
//  TaskExecutor taskExecutor2;
  @Resource
  AmazonDaemon amazonDaemon;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    amazonDaemon.connect();
    taskExecutor1.execute(() -> amazonDaemon.startWorldReceiverThread());
//    taskExecutor1.execute(() -> amazonDaemon.initWorldSenderThread());
  }
}
