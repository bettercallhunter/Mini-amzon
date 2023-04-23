package org.mini_amazon.socket_servers;

import com.google.protobuf.Message;

import org.mini_amazon.enums.ShipmentStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.proto.AmazonUPSProtocol;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.services.ShipmentService;
import org.mini_amazon.services.WarehouseService;
import org.mini_amazon.utils.AMessageBuilder;
import org.mini_amazon.utils.GPBUtil;
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
  public final InputStream AWInputStream;
  public final OutputStream AWOutputStream;

  public final InputStream AUInputStream;
  public final OutputStream AUOutputStream;
  private final Map<Long, Timer> msgTracker;
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
      this.AUInputStream = null;
      this.AUOutputStream = null;

      this.AWInputStream = this.AWSocket.getInputStream();
      this.AWOutputStream = this.AWSocket.getOutputStream();
      this.msgTracker = new ConcurrentHashMap<>();
      this.seqNum = 0;
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

  public synchronized void handleAResponses(WorldAmazonProtocol.AResponses aResponses) {
//    for (long seq : aResponses.getAcksList()) {
////      System.out.println("receive ack, seq: " + seq);
//      if (this.msgTracker.containsKey(seq)) {
////        System.out.println("remove timer, seq: " + seq);
//        this.msgTracker.get(seq).cancel();
//        this.msgTracker.remove(seq);
//      }
//    }
    this.ackHandler(aResponses.getAcksList());
    this.sendAckToWorld(aResponses);

    aResponses.getArrivedList().forEach(this::handleAPurchaseMore);
    aResponses.getReadyList().forEach(this::handleAPacked);
    aResponses.getLoadedList().forEach(this::handleALoaded);
    aResponses.getErrorList().forEach(this::handleAError);
    aResponses.getPackagestatusList().forEach(this::handleAPackageStatus);

    if (aResponses.hasFinished()) {
      System.out.println("Amazon disconnect finished. ");
    }
  }

  public synchronized void handleUACommands(AmazonUPSProtocol.UACommand uaCommands) {
    this.ackHandler(uaCommands.getAcksList());
    this.sendAckToUPS(uaCommands);
//    uaCommands.getLoadRequestsList().forEach(this::handleUALoadRequest);
//    uaCommands.getDeliveredList().forEach(this::handleUADelivered);
//    uaCommands.getErrorList().forEach(this::handleUAError);
  }

  public synchronized void ackHandler(List<Long> acks) {
    for (long seq : acks) {
//      System.out.println("receive ack, seq: " + seq);
      if (this.msgTracker.containsKey(seq)) {
//        System.out.println("remove timer, seq: " + seq);
        this.msgTracker.get(seq).cancel();
        this.msgTracker.remove(seq);
      }
    }
  }

  @Async("taskExecutor1")
  // arrived
  public void handleAPurchaseMore(WorldAmazonProtocol.APurchaseMore aPurchaseMore) {
    List<WorldAmazonProtocol.AProduct> aProducts = aPurchaseMore.getThingsList();
    Shipment shipment;
    try {
      shipment = shipmentService.getPendingShipmentBySameOrder(aPurchaseMore);
    } catch (ServiceError e) {
      /// will never reach here
      e.printStackTrace();
      return;
    }
    long seqNum = this.getSeqNum();
    WorldAmazonProtocol.APack aPack = AMessageBuilder.createAPack(aPurchaseMore.getWhnum(), aProducts, shipment.getId(), seqNum);
    this.sendToPackRequest(List.of(aPack), seqNum);
//    long newSeqNum = this.getSeqNum();
//    this.sendQueryRequest(List.of(AMessageBuilder.createAQuery(shipment.getId(), newSeqNum)), newSeqNum);
    //TODO:send ups
  }

  @Async("taskExecutor1")
  // ready
  public void handleAPacked(WorldAmazonProtocol.APacked aPacked) {
    Shipment shipment;
    try {
      shipment = shipmentService.updateShipmentStatus(aPacked.getShipid(), ShipmentStatus.PACKED);
    } catch (ServiceError e) {
      e.printStackTrace();
      return;
    }
    long seqNum = this.getSeqNum();
    Integer truckId = shipment.getTruckId();
    if (truckId != null) {
      this.sendLoadRequest(List.of(AMessageBuilder.createAPutOnTruck(shipment.getWarehouse().getId(), truckId, shipment.getId(), seqNum)), seqNum);
    }
    // TODO: send ups
//    long newSeqNum = this.getSeqNum();
//    this.sendQueryRequest(List.of(AMessageBuilder.createAQuery(shipment.getId(), newSeqNum)), newSeqNum);

  }

  @Async("taskExecutor1")
  public void handleALoaded(WorldAmazonProtocol.ALoaded aLoaded) {
    Shipment shipment;
    try {
      shipment = shipmentService.updateShipmentStatus(aLoaded.getShipid(), ShipmentStatus.LOADED);
    } catch (ServiceError e) {
      e.printStackTrace();
      return;
    }
    long seqNum = this.getSeqNum();
    // TODO: send ups
    try {
      shipment = shipmentService.updateShipmentStatus(aLoaded.getShipid(), ShipmentStatus.SHIPPING);
    } catch (ServiceError e) {
      e.printStackTrace();
    }
  }

  @Async("taskExecutor1")
  public void handleAError(WorldAmazonProtocol.AErr aError) {
    System.out.println("Amazon error: " + aError);
  }

  @Async("taskExecutor1")
  public void handleAPackageStatus(WorldAmazonProtocol.APackage aPackageStatus) {
    System.out.println("Amazon package status: " + aPackageStatus);
  }


  public synchronized long getSeqNum() {
    return this.seqNum++;
  }

  @Async("taskExecutor1")
  public void sendBuyRequest(List<WorldAmazonProtocol.APurchaseMore> aPurchaseMores, long seqNum) {
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllBuy(aPurchaseMores).build();
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

  public void sendAckToUPS(AmazonUPSProtocol.UACommand responses) {
    List<Long> acks = new ArrayList<>();
    acks.addAll(responses.getLoadRequestsList().stream().map(AmazonUPSProtocol.UALoadRequest::getSeqNum).toList());
    acks.addAll(responses.getDeliveredList().stream().map(AmazonUPSProtocol.UADelivered::getSeqNum).toList());
    acks.addAll(responses.getErrorList().stream().map(AmazonUPSProtocol.Err::getSeqNum).toList());

    if (!acks.isEmpty()) {
      AmazonUPSProtocol.AUCommand.Builder aCommands = AmazonUPSProtocol.AUCommand.newBuilder();
      aCommands.addAllAcks(acks);
      this.sendToUPS(aCommands.build());
    }
  }

  private void sendToWorld(WorldAmazonProtocol.AConnect aConnect) {
    this.sendTo(aConnect, this.AWOutputStream);
  }

  private void sendToWorld(WorldAmazonProtocol.ACommands command) {
    this.sendTo(command, this.AWOutputStream);
  }

  private void sendToWorld(WorldAmazonProtocol.ACommands command, long seq) {
    this.sendTo(command, this.AWOutputStream, seq);
  }

  private void sendToUPS(AmazonUPSProtocol.AUCommand command) {
    this.sendTo(command, this.AUOutputStream);
  }

  private void sendToUPS(AmazonUPSProtocol.AUCommand command, long seq) {
    this.sendTo(command, this.AUOutputStream, seq);
  }

  private void sendTo(Message message, OutputStream outputStream) {
    synchronized (AmazonDaemon.this) {
      GPBUtil.send(message, outputStream);
    }
  }

  private void sendTo(Message message, OutputStream outputStream, long seqNum) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        synchronized (AmazonDaemon.this) {
          GPBUtil.send(message, outputStream);
        }
      }
    }, 0, TIME_OUT);
    this.msgTracker.put(seqNum, timer);
  }
}
