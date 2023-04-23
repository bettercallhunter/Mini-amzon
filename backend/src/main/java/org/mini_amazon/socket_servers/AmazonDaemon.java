package org.mini_amazon.socket_servers;

import com.google.protobuf.Message;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.services.ShipmentService;
import org.mini_amazon.services.WarehouseService;
import org.mini_amazon.utils.AMessageBuilder;
import org.mini_amazon.utils.GPBUtil;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Resource;

@Component
public class AmazonDaemon {

  public static final int TIME_OUT = 10000;
  private static final String WORLD_HOST = "localhost";
  private static final int WORLD_PORT = 23456;
  private static final String UPS_HOST = "localhost";
  private static final int UPS_PORT = 12345;
  //  public static final int AMAZON_SERVER_PORT = 9999;
//  //Server Communicate with World
  private final Socket AWSocket;
  private final Socket AUSocket;
  //  InputStream inputStream;
//  public final CodedInputStream aWcodedInputStream;
  public final InputStream AWInputStream;
  public final OutputStream AWoutputStream;
  private final Map<Long, Timer> msgTracker;
  //  private final Map<Long, Shipment> pendingShipmentTracker;
  private long seqNum;

  //  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
//  ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 5, TimeUnit.MILLISECONDS, workQueue);
  @Resource
  private WarehouseService warehouseService;
  @Resource
  private ShipmentService shipmentService;

  public AmazonDaemon() {
    try {
      this.AWSocket = new Socket(WORLD_HOST, WORLD_PORT);
//      this.AUSocket = new Socket(upsHost, upsPort);
      this.AUSocket = null;

      this.AWInputStream = this.AWSocket.getInputStream();
      this.AWoutputStream = this.AWSocket.getOutputStream();
      this.msgTracker = new ConcurrentHashMap<>();
//      this.pendingShipmentTracker = new ConcurrentHashMap<>();
      this.seqNum = 0;
//      this.connect();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void startWorldReceiverThread() {
    while (true) {
      WorldAmazonProtocol.AResponses.Builder responses = WorldAmazonProtocol.AResponses.newBuilder();
      GPBUtil.receiveFrom(responses, AmazonDaemon.this.AWInputStream);
      this.handleAResponses(responses.build());

    }
  }

  public void connect() {
//    List<Warehouse> warehouses = new ArrayList<>();
    List<Warehouse> warehouses = warehouseService.getAllWarehouses();
    List<WorldAmazonProtocol.AInitWarehouse> wh = warehouses.stream().map(w -> WorldAmazonProtocol.AInitWarehouse.newBuilder().setX(w.getX()).setY(w.getY()).setId(w.getId()).build()).toList();
    this.connectToNewWorld(wh);
    GPBUtil.receiveFrom(WorldAmazonProtocol.AConnected.newBuilder(), this.AWInputStream);
  }

  public void initWorldSenderThread() {
    int i = 0;
    while (true) {
      try {
//        System.out.println("send");
        Thread.sleep(3000);
//        WorldAmazonProtocol.APurchaseMore.Builder aPurchaseMoreBuilder = WorldAmazonProtocol.APurchaseMore.newBuilder();
//        aPurchaseMoreBuilder.setWhnum(1);
//        aPurchaseMoreBuilder.setSeqnum(seqNum);
//        aPurchaseMoreBuilder.addAllThings(new ArrayList<>());
//        System.out.println(aPurchaseMoreBuilder.build());
//        WorldAmazonProtocol.APurchaseMore message = aPurchaseMoreBuilder.build();
//        CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
////      System.out.println("send: " + message);
//        int size = message.getSerializedSize();
//        codedOutputStream.writeUInt32NoTag(size);
//        message.writeTo(codedOutputStream);
//        codedOutputStream.flush();

        this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), i++)), 0);
//        System.out.println("send finished");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  //  @Async
  public void initWorldReceiverThread() {
//    System.out.println("initWorldReceiverThread");
//    Thread recvThread = new Thread(() -> {

//      while (!Thread.currentThread().isInterrupted()) {
//    this.executor.execute(() -> {
    while (true) {
//      try {
//        WorldAmazonProtocol.AResponses.Bu aResponses =WorldAmazonProtocol.AResponses.newBuilder();
      GPBUtil.receiveFrom(WorldAmazonProtocol.AConnected.newBuilder(), this.AWInputStream);
//        System.out.println(aResponses);

//          this.handleAResponses(aResponses);
//          System.out.println("thread started");
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
    }
//    });
//      }
//    });
//    recvThread.start();
  }


  public synchronized void handleAResponses(WorldAmazonProtocol.AResponses aResponses) {
    for (long seq : aResponses.getAcksList()) {
//      System.out.println("receive ack, seq: " + seq);
      if (this.msgTracker.containsKey(seq)) {
//        System.out.println("remove timer, seq: " + seq);
        this.msgTracker.get(seq).cancel();
        this.msgTracker.remove(seq);
      }
    }
    sendAckToWorld(aResponses);
    //TODO
//    System.out.println("test+"+ aResponses.getArrivedList());

    aResponses.getArrivedList().forEach(this::handleAPurchaseMore);
//    aResponses.getReadyList().forEach(r -> new APackedConsumer().accept(r));
//    aResponses.getLoadedList().forEach(r -> new ALoadedConsumer().accept(r));
//    aResponses.getErrorList().forEach(r -> new AErrConsumer().accept(r));
//    aResponses.getPackagestatusList().forEach(r -> new APackageConsumer().accept(r));

    if (aResponses.hasFinished()) {
      System.out.println("Amazon disconnect finished. ");
    }
  }

  @Async("taskExecutor1")
  public void handleAPurchaseMore(WorldAmazonProtocol.APurchaseMore aPurchaseMore) {
//    System.out.println("Received APurchaseMore: " + aPurchaseMore);

    List<WorldAmazonProtocol.AProduct> aProducts = aPurchaseMore.getThingsList();
    Shipment shipment = null;
    try {
      shipment = shipmentService.getPendingShipmentBySameOrder(aPurchaseMore);
    } catch (ServiceError e) {
      /// will never reach here
      e.printStackTrace();
      return;
    }

    WorldAmazonProtocol.APack aPack = AMessageBuilder.createAPack(aPurchaseMore.getWhnum(), aProducts, shipment.getId(), aPurchaseMore.getSeqnum());
    this.sendToPackRequest(List.of(aPack), this.getSeqNum());
  }
  //  @Async("gpbReceiverExecutor")
//  @Async("taskExecutor2")
//  @PostConstruct
//  @Async
//  @PostConstruct


  public synchronized long getSeqNum() {
    return this.seqNum++;
  }

  @Async("taskExecutor1")
  public void sendBuyRequest(List<WorldAmazonProtocol.APurchaseMore> aPurchaseMores, long seqNum) {
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllBuy(aPurchaseMores).build();
//    for (WorldAmazonProtocol.APurchaseMore aPurchaseMore : aPurchaseMores) {
//
//      this.pendingShipmentTracker.put(aPurchaseMore.get, aPurchaseMores);
//    }
    this.sendToWorld(aCommands, seqNum);
  }

  @Async("taskExecutor1")
  public void sendToPackRequest(List<WorldAmazonProtocol.APack> aPacks, long seqNum) {
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllTopack(aPacks).build();
    this.sendToWorld(aCommands, seqNum);
  }

  @Async("taskExecutor1")
  public void sendLoadRequest(List<WorldAmazonProtocol.APutOnTruck> aLoads, long seqNum) {
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllLoad(aLoads).build();
    this.sendToWorld(aCommands, seqNum);
  }

  @Async("taskExecutor1")
  public void sendQueryRequest(List<WorldAmazonProtocol.AQuery> aQueries, long seqNum) {
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllQueries(aQueries).build();
    this.sendToWorld(aCommands, seqNum);
  }


  public void connectToNewWorld(List<WorldAmazonProtocol.AInitWarehouse> positions) {
    // create all warehouses based on the positions
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(null, positions);
    this.sendToWorld(aConnect);
  }

  public void connectToWorld(Long worldId) {
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(worldId, List.of());
    this.sendToWorld(aConnect);
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

  private void sendToWorld(WorldAmazonProtocol.AConnect aConnect) {
    synchronized (AmazonDaemon.this) {
      GPBUtil.send(aConnect, AmazonDaemon.this.AWoutputStream);
    }
  }

  private void sendToWorld(WorldAmazonProtocol.ACommands command) {
    synchronized (AmazonDaemon.this) {
      GPBUtil.send(command, AmazonDaemon.this.AWoutputStream);
    }
  }

  private void sendToWorld(WorldAmazonProtocol.ACommands command, long seq) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        synchronized (AmazonDaemon.this) {
          GPBUtil.send(command, AmazonDaemon.this.AWoutputStream);
        }
      }
    }, 0, TIME_OUT);
    this.msgTracker.put(seq, timer);
  }


  //   main
  public static void main(String[] args) {
    AmazonDaemon amazonDaemon = new AmazonDaemon();
//    try {
//    amazonDaemon.run();
//    amazonDaemon.thre
//
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//    new Thread(() -> {
//      try {
//        while (true) {
//          Thread.sleep(1000);
//          System.out.println("send buy request");
//
//        }
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }).start();
//    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//      System.out.println(aConnected);
//      return aConnected.toBuilder();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }

  }


}
