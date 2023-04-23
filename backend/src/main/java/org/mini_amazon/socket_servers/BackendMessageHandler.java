package org.mini_amazon.socket_servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.net.Socket;

public class BackendMessageHandler {
  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(100);
  ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 5, TimeUnit.MILLISECONDS, workQueue);

//  public BackendMessageHandler() {
//    this.setDaemon(true);
//  }

//  @Override
  public void run() {
    // this outermost while loop ensure our server will runAll "forever"
    // if any exceptions are thrown, go to the catch clause and then will close
    // the serve automatically, next loop, will restart the server
    while (!Thread.currentThread().isInterrupted()) {
      try (Server backendServerSocket = new Server(8888)) {
        System.out.println("Listening connection from front-end at 8888");
        while (!Thread.currentThread().isInterrupted()) {
          final Socket client_socket = backendServerSocket.acceptOrNull();
          if (client_socket == null) {
            continue;
          }
          executor.execute(() -> this.handlePurchaseRequest(client_socket));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void handlePurchaseRequest(Socket client_socket) {
    try {
      PrintWriter output = new PrintWriter(client_socket.getOutputStream(), true);
      InputStream inputStream = client_socket.getInputStream();
      System.out.println("Received purchase request from front-end");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
