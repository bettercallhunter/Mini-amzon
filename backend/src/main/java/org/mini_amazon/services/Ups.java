package org.mini_amazon.services;

import com.google.protobuf.CodedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.mini_amazon.proto.AmazonUPSProtocol.UAConnect;
import org.mini_amazon.proto.WorldAmazonProtocol.AResponses;
import org.mini_amazon.proto.WorldUPSProtocol.UCommands;
import org.mini_amazon.proto.WorldUPSProtocol.UConnect;
import org.mini_amazon.proto.WorldUPSProtocol.UConnected;
import org.mini_amazon.proto.WorldUPSProtocol.UDeliveryLocation;
import org.mini_amazon.proto.WorldUPSProtocol.UGoDeliver;
import org.mini_amazon.proto.WorldUPSProtocol.UGoPickup;
import org.mini_amazon.proto.WorldUPSProtocol.UInitTruck;
import org.mini_amazon.proto.WorldUPSProtocol.UResponses;
import org.mini_amazon.utils.GPBUtil;

public class Ups {

  private static final String HOST = "localhost";
  private static final int PORT = 12345;
  private static final int serverPORT = 8081;

  public long worldID;
  public int truckID;

  private final InputStream in;
  private final OutputStream out;
  private long seqNum;

  public Ups() throws IOException {
    worldID = -1;
    seqNum = 0;
    truckID = 1;
    //connect to world
    Socket socket = new Socket(HOST, PORT);
    in = socket.getInputStream();
    out = socket.getOutputStream();
  }

  public void connectToWorld(long worldID) throws IOException {
    UInitTruck.Builder builder = UInitTruck.newBuilder();
    builder.setId(1);
    builder.setX(1);
    builder.setY(1);
    UConnect.Builder connect = UConnect.newBuilder();
    connect.setWorldid(worldID);
    connect.addTrucks(builder.build());
    connect.setIsAmazon(false);
    UConnected.Builder connected = UConnected.newBuilder();
    UConnect connectMsg = connect.build();
    GPBUtil.send(connectMsg, out);
    seqNum++;
    GPBUtil.receiveFrom(connected, CodedInputStream.newInstance(in));
    UConnected connectedMsg = connected.build();
  }

  public void pickUp(long worldID, int whid, int packageid) throws IOException {
    UGoPickup.Builder pickup = UGoPickup.newBuilder();
    pickup.setWhid(whid);
    //truck id?
    pickup.setTruckid(packageid % 2 == 0 ? 1 : 2);
    pickup.setSeqnum(seqNum);

    UCommands.Builder commands = UCommands.newBuilder();
    commands.addPickups(pickup.build());
    UCommands commandsMsg = commands.build();
    GPBUtil.send(commandsMsg, out);
    seqNum++;
    UResponses.Builder responses = UResponses.newBuilder();
    GPBUtil.receiveFrom(responses, CodedInputStream.newInstance(in));

    //with amazon

    ServerSocket serverSocket = new ServerSocket(serverPORT);
    Socket socket = serverSocket.accept();
    OutputStream UAout = socket.getOutputStream();
    InputStream UAin = socket.getInputStream();
  }

  public void delivery(int X, int Y, int packageid) throws IOException {
    UGoDeliver.Builder deliver = UGoDeliver.newBuilder();
    deliver.setTruckid(packageid % 2 == 0 ? 1 : 2);
    deliver.setSeqnum(seqNum);
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
    UResponses.Builder responses = UResponses.newBuilder();
    GPBUtil.receiveFrom(responses, CodedInputStream.newInstance(in));
  }

  public void run() throws Exception {
    connectToWorld(1);
    //connect to amazon
    ServerSocket serverSocket = new ServerSocket(serverPORT);
    Socket socket = serverSocket.accept();
    OutputStream UAout = socket.getOutputStream();
    InputStream UAin = socket.getInputStream();
    UAConnect.Builder connect = UAConnect
      .newBuilder()
      .setWorldid(1)
      .setSeqNum(seqNum);
    GPBUtil.send(connect.build(), UAout);
    AResponses.Builder response = AResponses.newBuilder();
    GPBUtil.receiveFrom(response, CodedInputStream.newInstance(UAin));
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
//   public Ups(
//     String worldHost,
//     int worldPort,
//     String amazonHost,
//     int amazonPort
//   ) throws Exception {
//     this.worldHost = worldHost;
//     this.worldPort = worldPort;
//     this.amazonHost = amazonHost;
//     this.amazonPort = amazonPort;
//     this.upsTrucksSocket = new Socket(this.worldHost, this.worldPort);
//     ServerSocket serverSocket = new ServerSocket(this.amazonPort);
//     this.UASocket =  serverSocket.accept();
//     this.outputStream = this.upsTrucksSocket.getOutputStream();
//     final InputStream inputStream = this.upsTrucksSocket.getInputStream();
//     this.codedInputStream = CodedInputStream.newInstance(inputStream);
//     this.seqNum = 0;
//   }
//   public void run() {
//     WorldUPSProtocol.UInitTruck truck = WorldUPSProtocol.UInitTruck
//       .newBuilder()
//       .setX(1)
//       .setY(1)
//       .setId(1)
//       .build();
//     this.connectToTrucks(5L);
//     try {
//       GPBUtil.receiveFrom(
//         WorldUPSProtocol.UConnected.newBuilder(),
//         this.codedInputStream
//       );
//       //      this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
//       //      this.receiveAResponses();
//       //      this.sendBuyRequest(List.of(),1L);
//     } catch (IOException e) {
//       throw new RuntimeException(e);
//     }
//   }
//   public void connectToTrucks(@NonNull Long worldId) {
//     WorldUPSProtocol.UConnect uConnect = UMessageBuilder.createNewWorld(
//       worldId,
//       List.of()
//     );
//     GPBUtil.send(uConnect, outputStream);
//   }
//   public void sendPickUpRequests(
//     @NonNull List<WorldUPSProtocol.UGoPickup> uGoPickup
//   ) {
//     //    this.seqNum++;
//     WorldUPSProtocol.UCommands uCommands = UMessageBuilder.createUCommands(
//       uGoPickup
//     );
//     GPBUtil.send(uCommands, outputStream);
//   }
// }
