package org.mini_amazon.services;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Server {
  public static final boolean DEBUG = true;
  //Server Communicate with World
  private final Socket amazonToWarehouseSocket;

  InputStream inputStream;
  CodedInputStream codedInputStream;
  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
  ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 5, TimeUnit.MILLISECONDS, workQueue);

  private final String worldHost;
  private final int worldPort;

  public Server(String worldHost, int worldPort, int serverToClientPortNum, String serverToAmazonHost, int serverToAmazonPortNum) throws IOException {
    //Establish Connection with World
    this.worldHost = worldHost;
    this.worldPort = worldPort;
    this.amazonToWarehouseSocket = new Socket(this.worldHost, this.worldPort);
    InputStream inputStream = this.amazonToWarehouseSocket.getInputStream();
    this.codedInputStream = CodedInputStream.newInstance(inputStream);
  }

  public void run() {
    this.sendAConnect(4L, new ArrayList<>());

    try {
      this.receiveAConnected();

      this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
      this.receiveAResponses();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

//    WorldAmazonProtocol.AConnected aConnected = null;
//    try {
//      aConnected = this.receiveAConnected();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    if (aConnected.getResult().equals("connected!")) {
//      System.out.println("Connected to world!");
//    } else {
//      this.sendAConnect(null, new ArrayList<>());
//    }
  }

  public void sendBuyRequest(@NonNull List<WorldAmazonProtocol.APurchaseMore> aPurchaseMores) {

    WorldAmazonProtocol.ACommands aCommands = AMessageBuilder.createACommands(aPurchaseMores, List.of(), List.of(), List.of(), List.of());
    try {
      this.send(aCommands);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void sendAConnect(@Nullable Long worldId, @NonNull List<Pair<Integer, Integer>> positions) {

    // create all warehouses based on the positions
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(worldId, positions);
    try {
      this.send(aConnect);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized WorldAmazonProtocol.AConnected receiveAConnected() throws IOException {
    WorldAmazonProtocol.AConnected aConnected = WorldAmazonProtocol.AConnected.parseFrom(this.codedInputStream.readByteArray());
    if (DEBUG) {
      System.out.println("receive: " + aConnected);
    }
    return aConnected;
  }

  public synchronized WorldAmazonProtocol.AResponses receiveAResponses() throws IOException {
    WorldAmazonProtocol.AResponses aResponses = WorldAmazonProtocol.AResponses.parseFrom(this.codedInputStream.readByteArray());
    if (DEBUG) {
      System.out.println("receive: " + aResponses);
    }
    return aResponses;
  }

  public synchronized void send(Message message) throws IOException {
    final OutputStream outputStream = this.amazonToWarehouseSocket.getOutputStream();
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
    int size = message.getSerializedSize();
    codedOutputStream.writeUInt32NoTag(size);
    message.writeTo(codedOutputStream);
    if (DEBUG) {
      System.out.println("send: " + message);
    }
    codedOutputStream.flush();
  }

  // main
  public static void main(String[] args) {
    Server server = null;
    try {
      server = new Server("localhost", 23456, 8081, "localhost", 8082);
      server.run();
//      System.out.println(aConnected);
//      return aConnected.toBuilder();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
