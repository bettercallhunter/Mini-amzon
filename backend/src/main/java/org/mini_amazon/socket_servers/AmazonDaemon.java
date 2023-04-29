package org.mini_amazon.socket_servers;

import com.google.protobuf.Message;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.enums.ShipmentStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.models.Order;
import org.mini_amazon.proto.AmazonUPSProtocol;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.services.OrderService;
import org.mini_amazon.services.ShipmentService;
import org.mini_amazon.services.WarehouseService;
import org.mini_amazon.utils.AMessageBuilder;
import org.mini_amazon.utils.GPBUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

@Component
public class AmazonDaemon {

  public static final int TIME_OUT = 5000;
  private static final String WORLD_HOST = "vcm-32169.vm.duke.edu";
  private static final int WORLD_PORT = 23456;
  private static final int SOCKET_PORT = 8080;
  //  private static final String UPS_HOST = "localhost";
//  private static final int UPS_PORT = 8081;
  // public static final int AMAZON_SERVER_PORT = 9999;
  // //Server Communicate with World
  private final Socket AWSocket;
  private final ServerSocket serverSocket;
  public final InputStream AWInputStream;
  public final OutputStream AWOutputStream;

  public InputStream AUInputStream;
  public OutputStream AUOutputStream;
  private final Map<Long, Timer> msgTracker;
  private long seqNum;
  // at most one
  private final HashMap<Long, Object> receivedSeq;

  private Long worldId;

  //  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
//  ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 5,
//          TimeUnit.MILLISECONDS, workQueue);
  @Resource
  private WarehouseService warehouseService;
  @Resource
  private ShipmentService shipmentService;
  @Resource
  private OrderService orderService;
  @Resource
  TaskExecutor taskExecutor1;


  public AmazonDaemon() {
    try {
      this.AWSocket = new Socket(WORLD_HOST, WORLD_PORT);
      this.serverSocket = new ServerSocket(SOCKET_PORT);
      // this.AUSocket = null;
//       this.AUInputStream = AUSocket.getInputStream();
//       this.AUOutputStream = AUSocket.getOutputStream();

      this.AWInputStream = this.AWSocket.getInputStream();
      this.AWOutputStream = this.AWSocket.getOutputStream();
      this.msgTracker = new ConcurrentHashMap<>();
      this.receivedSeq = new HashMap<>();
      this.seqNum = 0;
//      this.wordId =
      System.out.println("Amazon Socket Server is running on port " + SOCKET_PORT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Async("taskExecutor1")
  public void run() {
    try {
      final Socket client_socket = this.serverSocket.accept();
      if (client_socket == null) {
        throw new RuntimeException("client_socket is null");
      }

      this.AUInputStream = client_socket.getInputStream();
      this.AUOutputStream = client_socket.getOutputStream();
      AmazonUPSProtocol.UAStart.Builder uaStart = AmazonUPSProtocol.UAStart.newBuilder();
      boolean received = GPBUtil.receiveFrom(uaStart, this.AUInputStream);
      if (!received) {
        System.out.println("UAStart not received");
//        throw new RuntimeException("UAStart not received");
        client_socket.close();
        run();
        return;
      }
      System.out.println("worldId: " + uaStart.getWorldid());
      this.worldId = uaStart.getWorldid();
      this.connect(uaStart.getWorldid());
      taskExecutor1.execute(this::startWorldReceiverThread);
      taskExecutor1.execute(this::startUPSReceiverThread);
    } catch (IOException e) {
      e.printStackTrace();
//      throw new RuntimeException(e);
    }
  }

  //  @Async("taskExecutor1")
  public void startWorldReceiverThread() {
    while (true) {

      WorldAmazonProtocol.AResponses.Builder responses = WorldAmazonProtocol.AResponses.newBuilder();
      boolean received = GPBUtil.receiveFrom(responses, AmazonDaemon.this.AWInputStream);
      if (!received) {
        this.connect(this.worldId);
      }
      this.handleAResponses(responses.build());
    }
  }

  //  @Async("taskExecutor1")
  public void startUPSReceiverThread() {

//    while (!Thread.currentThread().isInterrupted()) {
//    try {


    // do something with client_socket

    while (true) {
      AmazonUPSProtocol.UACommand.Builder responses = AmazonUPSProtocol.UACommand.newBuilder();

      GPBUtil.receiveFrom(responses, AUInputStream);
      AmazonDaemon.this.handleUACommands(responses.build());
    }

  }


//  } catch(
//  IOException e)
//
//  {
//    e.printStackTrace();
//  } finally
//
//  {
//    try {
//      client_socket.close();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }


  /**
   * This is a helper method to accept a socket from the ServerSocket or return null if it
   * timeouts.
   *
   * @return the socket accepted from the ServerSocket
   */
//  public Socket acceptOrNull() {
//    try {
//      return this.serverSocket.accept();
//    } catch (IOException ioe) {
//      return null;
//    }
//  }
  @Async("taskExecutor1")
  public void connect(Long worldId) {


    // connect warehouses

    List<Warehouse> warehouses = warehouseService.getAllWarehouses();
    List<WorldAmazonProtocol.AInitWarehouse> wh = warehouses.stream()
            .map(
                    w -> WorldAmazonProtocol.AInitWarehouse.newBuilder().setX(w.getX()).setY(w.getY()).setId(w.getId()).build())
            .toList();

    this.connectToWorld(wh, worldId);
    WorldAmazonProtocol.AConnected.Builder result = WorldAmazonProtocol.AConnected.newBuilder();
    GPBUtil.receiveFrom(result, this.AWInputStream);

    boolean isSuccess = result.getResult().equals("connected!");
    if (isSuccess) {
      System.out.println("connected to world");
    } else {
      System.out.println("failed to connect to world");
//      throw new RuntimeException("failed to connect to world");
    }
  }

  public synchronized void handleAResponses(WorldAmazonProtocol.AResponses aResponses) {
    // for (long seq : aResponses.getAcksList()) {
    //// System.out.println("receive ack, seq: " + seq);
    // if (this.msgTracker.containsKey(seq)) {
    //// System.out.println("remove timer, seq: " + seq);
    // this.msgTracker.get(seq).cancel();
    // this.msgTracker.remove(seq);
    // }
    // }


    this.ackHandler(aResponses.getAcksList());
    this.sendAckToWorld(aResponses);

    aResponses.getArrivedList().forEach(this::handleAPurchaseMore);
    aResponses.getReadyList().forEach(this::handleAPacked);
    aResponses.getLoadedList().forEach(this::handleALoaded);
    aResponses.getErrorList().forEach(this::handleAError);
    aResponses.getPackagestatusList().forEach(this::handleAPackageStatus);


    if (aResponses.hasFinished()) {
      System.out.println("Amazon disconnect finished. ");
      try {
        this.AWSocket.close();
//        this.AU
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public synchronized void handleUACommands(AmazonUPSProtocol.UACommand uaCommands) {
    this.ackHandler(uaCommands.getAcksList());
    this.sendAckToUPS(uaCommands);
    uaCommands.getLoadRequestsList().stream().filter(l -> !this.receivedSeq.containsKey(l.getSeqNum())).forEach(this::handleUALoadRequest);
    uaCommands.getDeliveredList().stream().filter(l -> !this.receivedSeq.containsKey(l.getSeqNum())).forEach(this::handleUADelivered);
    uaCommands.getErrorList().stream().filter(l -> !this.receivedSeq.containsKey(l.getSeqNum())).forEach(this::handleUAError);

    for (long i : uaCommands.getAcksList()) {
      this.receivedSeq.put(i, true);
    }
  }

  public synchronized void ackHandler(List<Long> acks) {
    for (long seq : acks) {
      if (this.msgTracker.containsKey(seq)) {
        // System.out.println("remove timer, seq: " + seq);
        this.msgTracker.get(seq).cancel();
        this.msgTracker.remove(seq);
      }
    }
  }

  @Async("taskExecutor1")
  public void handleUALoadRequest(AmazonUPSProtocol.UALoadRequest uaLoadRequest) {
    long seqNum = this.getSeqNum();
    try {
      Shipment shipment = shipmentService.getShipmentById(uaLoadRequest.getShipId());
      shipment = shipmentService.updateShipmentTruckId(shipment.getId(), uaLoadRequest.getTruckId());
      if (shipment.getStatus() == ShipmentStatus.PACKED) {
        this.sendLoadRequest(List.of(AMessageBuilder.createAPutOnTruck(shipment.getWarehouse().getId(),
                shipment.getTruckId(), shipment.getId(), seqNum)), seqNum);
      }
    } catch (ServiceError e) {
      AmazonUPSProtocol.AUCommand.Builder aCommand = AmazonUPSProtocol.AUCommand.newBuilder();
      AmazonUPSProtocol.Err.Builder err = AmazonUPSProtocol.Err.newBuilder().setErr(
                      "Shipment not found, id: "
                      + uaLoadRequest.getShipId())
              .setSeqNum(seqNum).setOriginSeqNum(uaLoadRequest.getSeqNum());
      aCommand.addError(err);
      this.sendToUPS(aCommand.build(), seqNum);
    }
  }

  @Async("taskExecutor1")
  public void handleUADelivered(AmazonUPSProtocol.UADelivered uaDelivered) {
    try {
      Shipment shipment = shipmentService.getShipmentById(uaDelivered.getShipId());
      shipmentService.updateShipmentStatus(shipment.getId(), ShipmentStatus.DELIVERED);

    } catch (ServiceError e) {
      AmazonUPSProtocol.AUCommand.Builder aCommand = AmazonUPSProtocol.AUCommand.newBuilder();
      AmazonUPSProtocol.Err.Builder err = AmazonUPSProtocol.Err.newBuilder().setErr(
                      "Shipment not found, id: "
                      + uaDelivered.getShipId())
              .setSeqNum(seqNum).setOriginSeqNum(uaDelivered.getSeqNum());
      aCommand.addError(err);
      this.sendToUPS(aCommand.build(), seqNum);
    }
  }

  @Async("taskExecutor1")
  public void handleUAError(AmazonUPSProtocol.Err err) {
    System.out.println("UPS error: " + err);
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
    long seqNum1 = this.getSeqNum();
    WorldAmazonProtocol.APack aPack = AMessageBuilder.createAPack(aPurchaseMore.getWhnum(), aProducts, shipment.getId(),
            seqNum1);
    this.sendToPackRequest(List.of(aPack), seqNum1);
    // send to ups
    long seqNum2 = this.getSeqNum();
    AmazonUPSProtocol.AUPickupRequest.Builder aUPickupRequest = AmazonUPSProtocol.AUPickupRequest.newBuilder();
    aUPickupRequest.setSeqNum(seqNum2);
    aUPickupRequest.setShipId(shipment.getId());
    aUPickupRequest.setWarehouseId(shipment.getWarehouse().getId());
    aUPickupRequest.setX(shipment.getWarehouse().getX());
    aUPickupRequest.setY(shipment.getWarehouse().getY());
    aUPickupRequest.setDestinationX(shipment.getDestinationX());
    aUPickupRequest.setDestinationY(shipment.getDestinationY());
    String upsName = shipment.getUpsName();
    if (upsName != null && !upsName.isEmpty()) {
      aUPickupRequest.setUpsName(shipment.getUpsName());
    }
    aUPickupRequest.addAllItems(shipment.getOrders().stream().map(o -> o.getItem().getName()).collect(Collectors.toList()));

    this.sendAUPickUpRequest(List.of(aUPickupRequest.build()), seqNum2);
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
      this.sendLoadRequest(
              List.of(
                      AMessageBuilder.createAPutOnTruck(shipment.getWarehouse().getId(), truckId, shipment.getId(), seqNum)),
              seqNum);
    }
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
    AmazonUPSProtocol.AUDeliverRequest.Builder uADelivered = AmazonUPSProtocol.AUDeliverRequest.newBuilder();
    uADelivered.setSeqNum(seqNum);
    uADelivered.setShipId(shipment.getId());
    this.sendAUDeliverRequest(List.of(uADelivered.build()), seqNum);
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
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllBuy(aPurchaseMores)
            .build();
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
    WorldAmazonProtocol.ACommands aCommands = WorldAmazonProtocol.ACommands.newBuilder().addAllQueries(aQueries)
            .build();
    this.sendToWorld(aCommands, seqNum);
  }

  @Async("taskExecutor1")
  public void sendDisconnectRequest(long seqNum) {
    WorldAmazonProtocol.ACommands aDisconnect = WorldAmazonProtocol.ACommands.newBuilder().setDisconnect(true).build();
    this.sendToWorld(aDisconnect, seqNum);
  }

  @Async("taskExecutor1")
  // send to ups to pickup the package
  public void sendAUPickUpRequest(List<AmazonUPSProtocol.AUPickupRequest> pickupRequests, long seqNum) {
    AmazonUPSProtocol.AUCommand auCommand = AmazonUPSProtocol.AUCommand.newBuilder()
            .addAllPickupRequests(pickupRequests).build();
    this.sendToUPS(auCommand, seqNum);
  }

  @Async("taskExecutor1")
  public void sendAUDeliverRequest(List<AmazonUPSProtocol.AUDeliverRequest> deliverRequests, long seqNum) {
    AmazonUPSProtocol.AUCommand auCommand = AmazonUPSProtocol.AUCommand.newBuilder()
            .addAllDeliverRequests(deliverRequests).build();
    this.sendToUPS(auCommand, seqNum);
  }

  public void connectToNewWorld(List<WorldAmazonProtocol.AInitWarehouse> positions) {
    // create all warehouses based on the positions
    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(null, positions);
    this.sendToWorld(aConnect);
  }

  public void connectToWorld(List<WorldAmazonProtocol.AInitWarehouse> positions, Long worldId) {

    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(worldId, positions);
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
      WorldAmazonProtocol.ACommands aCommands = AMessageBuilder.createACommands(List.of(), List.of(), List.of(),
              List.of(), acks);
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
    try {
      this.sendTo(command, this.AWOutputStream, seq);
    } catch (IllegalArgumentException e) {
      this.connect(this.worldId);
      this.sendTo(command, this.AWOutputStream, seq);
    }
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

  private void sendTo(Message message, OutputStream outputStream, long seqNum) throws IllegalArgumentException {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        synchronized (AmazonDaemon.this) {
          boolean success = GPBUtil.send(message, outputStream);
          if (!success) {
            throw new IllegalArgumentException("Failed to send message to world");
          }
        }
      }
    }, 0, TIME_OUT);
    this.msgTracker.put(seqNum, timer);
  }
}
