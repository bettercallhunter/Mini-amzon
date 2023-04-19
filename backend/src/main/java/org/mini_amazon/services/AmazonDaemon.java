package org.mini_amazon.services;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Message;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.utils.AErrConsumer;
import org.mini_amazon.utils.ALoadedConsumer;
import org.mini_amazon.utils.APackageConsumer;
import org.mini_amazon.utils.APackedConsumer;
import org.mini_amazon.utils.APurchaseMoreConsumer;
import org.mini_amazon.utils.GPBUtil;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AmazonDaemon {
  public static final int TIME_OUT = 3000;
  //Server Communicate with World
  private final Socket amazonWarehouseSocket;
  private final Socket AUSocket;
  private long seqNum;
  //  InputStream inputStream;
  private final CodedInputStream codedInputStream;
  private final OutputStream outputStream;
  private final Map<Long, Timer> msgTracker;

  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
  ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 5, TimeUnit.MILLISECONDS, workQueue);

  private final String worldHost;
  private final int worldPort;
  private final String upsHost;
  private final int upsPort;

  public AmazonDaemon(String worldHost, int worldPort, String upsHost, int upsPort) throws IOException {
    //Establish Connection with World
    this.worldHost = worldHost;
    this.worldPort = worldPort;
    this.upsHost = upsHost;
    this.upsPort = upsPort;
    this.amazonWarehouseSocket = new Socket(this.worldHost, this.worldPort);
    this.AUSocket = new Socket(this.upsHost, this.upsPort);

    final InputStream inputStream = this.amazonWarehouseSocket.getInputStream();
    this.codedInputStream = CodedInputStream.newInstance(inputStream);
    this.outputStream = this.amazonWarehouseSocket.getOutputStream();
    this.msgTracker = new ConcurrentHashMap<>();
    this.seqNum = 0;
  }

  public void run() {
//    List<AMessageBuilder.WareHouse> wh = new ArrayList<>();
    WorldAmazonProtocol.AInitWarehouse aInitWarehouse = WorldAmazonProtocol.AInitWarehouse.newBuilder().setX(1).setY(1).setId(1).build();
//    wh.add(new AMessageBuilder.WareHouse(1, 1, 1));

//    this.sendAConnect(null, wh);
    this.connectToWorld(5L);

    try {
      GPBUtil.receiveFrom(WorldAmazonProtocol.AConnected.newBuilder(), this.codedInputStream);

//      this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//      this.receiveAResponses();
//      this.sendBuyRequest(List.of(),1L);
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
//    this.seqNum++;
    WorldAmazonProtocol.ACommands aCommands = AMessageBuilder.createACommands(aPurchaseMores, List.of(), List.of(), List.of(), List.of());
    this.sendToWorld(aCommands);

  }

  public void connectToNewWorld(@NonNull List<WorldAmazonProtocol.AInitWarehouse> positions) {

    // create all warehouses based on the positions
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(null, positions);
    this.sendToWorld(aConnect);
  }

  public void connectToWorld(@NonNull Long worldId) {
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(worldId, List.of());
    this.sendToWorld(aConnect);
  }

  public void handleAResponses(WorldAmazonProtocol.AResponses aResponses) {
    for (long seq : aResponses.getAcksList()) {
      if (this.msgTracker.containsKey(seq)) {
        this.msgTracker.get(seq).cancel();
        this.msgTracker.remove(seq);
      }
    }
    sendAckToWorld(aResponses);

    aResponses.getArrivedList().forEach(r -> new APurchaseMoreConsumer().accept(r));
    aResponses.getReadyList().forEach(r -> new APackedConsumer().accept(r));
    aResponses.getLoadedList().forEach(r -> new ALoadedConsumer().accept(r));
    aResponses.getErrorList().forEach(r -> new AErrConsumer().accept(r));
    aResponses.getPackagestatusList().forEach(r -> new APackageConsumer().accept(r));

    if (aResponses.hasFinished()) {
      System.out.println("Amazon disconnect finished. ");
    }
  }

  public void sendAckToWorld(WorldAmazonProtocol.AResponses responses) {
    List<Long> acks = new ArrayList<>();
    acks.addAll(responses.getArrivedList().stream().map(WorldAmazonProtocol.APurchaseMore::getSeqnum).toList());
    acks.addAll(responses.getReadyList().stream().map(WorldAmazonProtocol.APacked::getSeqnum).toList());
    acks.addAll(responses.getLoadedList().stream().map(WorldAmazonProtocol.ALoaded::getSeqnum).toList());
    acks.addAll(responses.getErrorList().stream().map(WorldAmazonProtocol.AErr::getSeqnum).toList());
    acks.addAll(responses.getPackagestatusList().stream().map(WorldAmazonProtocol.APackage::getSeqnum).toList());
    if (!acks.isEmpty()) {
      WorldAmazonProtocol.ACommands aCommands = AMessageBuilder.createACommands(List.of(), List.of(), List.of(), List.of(), acks);
      this.sendToWorld(aCommands);
    }
  }


  private void sendToWorld(Message commands) {
    Timer timer = new Timer();
    timer.schedule(
            new TimerTask() {
              @Override
              public void run() {
                synchronized (AmazonDaemon.this) {
                  GPBUtil.send(commands, AmazonDaemon.this.outputStream);
                }
              }
            },
            0,
            TIME_OUT);
    this.msgTracker.put(this.seqNum, timer);
  }


  // main
  public static void main(String[] args) {
    AmazonDaemon amazonDaemon;
    try {
      amazonDaemon = new AmazonDaemon("localhost", 23456, "localhost", 8081);
      amazonDaemon.run();
//      System.out.println(aConnected);
//      return aConnected.toBuilder();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
