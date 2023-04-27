package org.mini_amazon.runner;

import org.mini_amazon.socket_servers.AmazonDaemon;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

@Component
public class AppStart implements ApplicationRunner {
  public static int CONNECTION_TIMEOUT_EXIT = 5000;
  public static int RETRY_INTERVAL = 1000;

  private ScheduledExecutorService executorService;
  @Resource
  TaskExecutor taskExecutor1;
  // @Resource
  // TaskExecutor taskExecutor2;
  @Resource
  AmazonDaemon amazonDaemon;

  @Override
  public void run(ApplicationArguments args) {
// boolean success = amazonDaemon.connect();
// if (!success) {
// System.out.println("Connection failed");
// System.exit(1);
// }
taskExecutor1.execute(() -> amazonDaemon.run());
//    taskExecutor1.execute(() -> amazonDaemon.startUPSReceiverThread());
//    taskExecutor1.execute(() -> amazonDaemon.startWorldReceiverThread());
    // taskExecutor1.execute(() -> amazonDaemon.initWorldSenderThread());
  }
}
