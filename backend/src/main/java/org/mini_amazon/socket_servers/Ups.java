package org.mini_amazon.socket_servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.midi.SysexMessage;

import org.mini_amazon.proto.AmazonUPSProtocol;
import org.mini_amazon.proto.AmazonUPSProtocol.AUCommand;
import org.mini_amazon.proto.AmazonUPSProtocol.AUDeliverRequest;
import org.mini_amazon.proto.AmazonUPSProtocol.AUPickupRequest;
import org.mini_amazon.proto.AmazonUPSProtocol.UACommand;
import org.mini_amazon.proto.AmazonUPSProtocol.UADelivered;
import org.mini_amazon.proto.AmazonUPSProtocol.UALoadRequest;
import org.mini_amazon.proto.WorldUPSProtocol.UCommands;
import org.mini_amazon.proto.WorldUPSProtocol.UConnect;
import org.mini_amazon.proto.WorldUPSProtocol.UConnected;
import org.mini_amazon.proto.WorldUPSProtocol.UDeliveryLocation;
import org.mini_amazon.proto.WorldUPSProtocol.UFinished;
import org.mini_amazon.proto.WorldUPSProtocol.UGoDeliver;
import org.mini_amazon.proto.WorldUPSProtocol.UGoPickup;
import org.mini_amazon.proto.WorldUPSProtocol.UInitTruck;
import org.mini_amazon.proto.WorldUPSProtocol.UResponses;
import org.mini_amazon.utils.GPBUtil;

public class Ups {

  private static final String HOST = "localhost";
  private static final int PORT = 12345;
  private static final int serverPORT = 8081;
  Socket WorldSocket;
  Socket AUSocket;
  public long worldID;
  public int truckID;

  private final InputStream in;
  private final OutputStream out;
  private final InputStream AUin;
  private final OutputStream AUout;

  private long seqNum;

  public Ups() throws IOException {
    worldID = -1;
    seqNum = 0;
    truckID = 1;
    // connect to world
    WorldSocket = new Socket(HOST, PORT);
    in = WorldSocket.getInputStream();
    out = WorldSocket.getOutputStream();
    // connect to amazon

    ServerSocket socket = new ServerSocket(serverPORT);
    Socket AUSocket = socket.accept();
    AUout = AUSocket.getOutputStream();
    AUin = AUSocket.getInputStream();
  }

  public void connectToWorld() throws IOException {
    UInitTruck.Builder truck1 = UInitTruck.newBuilder();
    truck1.setId(1);
    truck1.setX(1);
    truck1.setY(1);
    UInitTruck.Builder truck2 = UInitTruck.newBuilder();
    truck2.setId(2);
    truck2.setX(2);
    truck2.setY(2);
    UConnect.Builder connect = UConnect.newBuilder();
    connect.addTrucks(truck1.build());
    connect.addTrucks(truck2.build());
    connect.setIsAmazon(false);
    UConnected.Builder connected = UConnected.newBuilder();
    UConnect connectMsg = connect.build();
    GPBUtil.send(connectMsg, out);
    seqNum++;
    GPBUtil.receiveFrom(connected, in);
    UConnected connectedMsg = connected.build();
    worldID = connectedMsg.getWorldid();
    System.out.println("worldID: " + worldID);

  }

  public void connectToAmazon() {

    AmazonUPSProtocol.UAStart.Builder connect = AmazonUPSProtocol.UAStart
        .newBuilder()
        .setWorldid(worldID)
        .setSeqnum(seqNum);
    GPBUtil.send(connect.build(), AUout);
    AmazonUPSProtocol.AUCommand.Builder response = AmazonUPSProtocol.AUCommand.newBuilder();
    System.out.println("recving from amazon");
    GPBUtil.receiveFrom(response, AUin);
    response.build();
    System.out.println("recved from amazon");
  }

  public void pickUp() throws IOException {
    AUCommand.Builder pickupRequest = AUCommand.newBuilder();
    GPBUtil.receiveFrom(pickupRequest, AUin);
    pickupRequest.build();
    // should be a loop
    AUPickupRequest pickupinfo = pickupRequest.getPickupRequests(0);
    long pickupSeq = pickupinfo.getSeqNum();
    long shipId = pickupinfo.getShipId();
    long warehouseId = pickupinfo.getWarehouseId();
    int x = pickupinfo.getX();
    int y = pickupinfo.getY();
    int destinationX = pickupinfo.getDestinationX();
    int destinationY = pickupinfo.getDestinationY();

    System.out.println("pickup seq: " + pickupSeq);
    // send message to AMAZON to confirm
    AmazonUPSProtocol.UACommand.Builder confirm = AmazonUPSProtocol.UACommand.newBuilder();
    confirm.addAcks(pickupSeq);
    GPBUtil.send(confirm.build(), AUout);

    // send message to WORLD to pick up
    UGoPickup.Builder pickup = UGoPickup.newBuilder();
    pickup.setWhid((int) warehouseId);
    // truck id?
    pickup.setTruckid(shipId % 2 == 0 ? 1 : 2);
    pickup.setSeqnum(seqNum);
    seqNum++;
    UCommands.Builder commands = UCommands.newBuilder();
    commands.addPickups(pickup.build());
    UCommands commandsMsg = commands.build();
    GPBUtil.send(commandsMsg, out);
    seqNum++;
    // recvive reponse from world
    UResponses.Builder responses = UResponses.newBuilder();
    GPBUtil.receiveFrom(responses, in);

    UALoadRequest.Builder loadRequest = UALoadRequest.newBuilder();
    loadRequest.setSeqNum(seqNum++);
    loadRequest.setShipId(shipId);
    loadRequest.setTruckId(shipId % 2 == 0 ? 1 : 2);
    UACommand.Builder response = UACommand.newBuilder();
    response.addLoadRequests(loadRequest);
    GPBUtil.send(response.build(), AUout);

    // UFinished finished = responses.getCompletions(0);

    // GPBUtil.receiveFrom(responses, CodedInputStream.newInstance(in));

    // with amazon
    delivery(destinationX, y, destinationY);
  }

  public void delivery(int X, int Y, int packageid) throws IOException {
    // recv AUDeliverRequest from amazon

    AUCommand.Builder deliverRequest = AUCommand.newBuilder();
    // AUDeliverRequest.Builder deliverRequest = AUDeliverRequest.newBuilder();
    AUDeliverRequest deliverinfo;
    while (true) {
      try {

        GPBUtil.receiveFrom(deliverRequest, AUin);
        deliverRequest.build();
        deliverinfo = deliverRequest.getDeliverRequests(0);
        break;
      } catch (Exception e) {
        continue;
      }
    }
    long shipId = deliverinfo.getShipId();
    // send ack to amazon
    AmazonUPSProtocol.UACommand confirmDeliverRequest = AmazonUPSProtocol.UACommand.newBuilder().addAcks(seqNum)
        .build();
    GPBUtil.send(confirmDeliverRequest, AUout);
    // send to World
    UGoDeliver.Builder deliver = UGoDeliver.newBuilder();
    deliver.setTruckid(packageid % 2 == 0 ? 1 : 2);
    deliver.setSeqnum(seqNum++);
    UDeliveryLocation.Builder location = UDeliveryLocation.newBuilder();
    location.setPackageid(packageid);
    location.setX(X);
    location.setY(Y);
    deliver.addPackages(location.build());
    UCommands.Builder commands = UCommands.newBuilder();
    commands.addDeliveries(deliver.build());
    UCommands commandsMsg = commands.build();
    GPBUtil.send(commandsMsg, out);
    seqNum++;
    // delivery made
    UResponses.Builder responses = UResponses.newBuilder();
    GPBUtil.receiveFrom(responses, in);

    // delivered
    UADelivered.Builder delivered = UADelivered.newBuilder();
    delivered.setSeqNum(seqNum++);
    delivered.setShipId(shipId);
    GPBUtil.send(delivered.build(), AUout);
    UACommand.Builder response = UACommand.newBuilder();
    GPBUtil.receiveFrom(response, AUin);
  }

  public void run() throws Exception {
    connectToWorld();
    connectToAmazon();

  }

  public static void main(String[] args) throws Exception {
    Ups ups = new Ups();
    ups.run();
    ups.pickUp();

  }
}
// public static final int TIME_OUT = 3000;
// public long worldID;
// public int truckID;
// private final CodedInputStream codedInputStream;
// private final OutputStream outputStream;
// private long seqNum;
// private final Socket upsTrucksSocket;
// private final Socket UASocket;
// private final String worldHost;
// private final int worldPort;
// private final String amazonHost;
// private final int amazonPort;
// public Ups(
// String worldHost,
// int worldPort,
// String amazonHost,
// int amazonPort
// ) throws Exception {
// this.worldHost = worldHost;
// this.worldPort = worldPort;
// this.amazonHost = amazonHost;
// this.amazonPort = amazonPort;
// this.upsTrucksSocket = new Socket(this.worldHost, this.worldPort);
// ServerSocket serverSocket = new ServerSocket(this.amazonPort);
// this.UASocket = serverSocket.accept();
// this.outputStream = this.upsTrucksSocket.getOutputStream();
// final InputStream inputStream = this.upsTrucksSocket.getInputStream();
// this.codedInputStream = CodedInputStream.newInstance(inputStream);
// this.seqNum = 0;
// }
// public void run() {
// WorldUPSProtocol.UInitTruck truck = WorldUPSProtocol.UInitTruck
// .newBuilder()
// .setX(1)
// .setY(1)
// .setId(1)
// .build();
// this.connectToTrucks(5L);
// try {
// GPBUtil.receiveFrom(
// WorldUPSProtocol.UConnected.newBuilder(),
// this.codedInputStream
// );
// // this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1,
// List.of(), 0)));
// // this.receiveAResponses();
// // this.sendBuyRequest(List.of(),1L);
// } catch (IOException e) {
// throw new RuntimeException(e);
// }
// }
// public void connectToTrucks(@NonNull Long worldId) {
// WorldUPSProtocol.UConnect uConnect = UMessageBuilder.createNewWorld(
// worldId,
// List.of()
// );
// GPBUtil.send(uConnect, outputStream);
// }
// public void sendPickUpRequests(
// @NonNull List<WorldUPSProtocol.UGoPickup> uGoPickup
// ) {
// // this.seqNum++;
// WorldUPSProtocol.UCommands uCommands = UMessageBuilder.createUCommands(
// uGoPickup
// );
// GPBUtil.send(uCommands, outputStream);
// }
// }
