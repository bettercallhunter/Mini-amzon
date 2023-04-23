package org.mini_amazon.socket_servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements AutoCloseable {
  public static final int TIME_OUT = 3000;
  ServerSocket serverSocket; // listen message from spring backend

  public Server(int portNum) throws IOException {
    serverSocket = new ServerSocket(portNum);
    serverSocket.setSoTimeout(TIME_OUT);
  }

  public Socket acceptOrNull(){
    try {
      return serverSocket.accept();
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public void close() throws IOException {
    serverSocket.close();
  }
}
