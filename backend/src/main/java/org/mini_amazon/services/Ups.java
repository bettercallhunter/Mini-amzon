package org.mini_amazon.services;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import org.mini_amazon.proto.WorldUPSProtocol;
import org.mini_amazon.proto.WorldUPSProtocol.UConnect;
import org.mini_amazon.proto.WorldUPSProtocol.UConnected;
import org.mini_amazon.utils.GPBUtil;
import org.springframework.lang.NonNull;

public class Ups {

  public static final int TIME_OUT = 3000;
  public long worldID;
  public int truckID;

  private final CodedInputStream codedInputStream;
  private final OutputStream outputStream;
  private long seqNum;

  private final Socket upsTrucksSocket;
  private final Socket UASocket;
  private final String worldHost;
  private final int worldPort;
  private final String amazonHost;
  private final int amazonPort;

  public Ups(
    String worldHost,
    int worldPort,
    String amazonHost,
    int amazonPort
  ) throws Exception {
    this.worldHost = worldHost;
    this.worldPort = worldPort;
    this.amazonHost = amazonHost;
    this.amazonPort = amazonPort;
    this.upsTrucksSocket = new Socket(this.worldHost, this.worldPort);
    this.UASocket = new Socket(this.amazonHost, this.amazonPort);
    this.outputStream = this.upsTrucksSocket.getOutputStream();
    final InputStream inputStream = this.upsTrucksSocket.getInputStream();
    this.codedInputStream = CodedInputStream.newInstance(inputStream);

    this.seqNum = 0;
  }

  public void run() {
    WorldUPSProtocol.UInitTruck truck = WorldUPSProtocol.UInitTruck
      .newBuilder()
      .setX(1)
      .setY(1)
      .setId(1)
      .build();
    this.connectToTrucks(5L);
    try {
      GPBUtil.receiveFrom(
        WorldUPSProtocol.UConnected.newBuilder(),
        this.codedInputStream
      );
      //      this.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(1, List.of(), 0)));
      //      this.receiveAResponses();
      //      this.sendBuyRequest(List.of(),1L);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void connectToTrucks(@NonNull Long worldId) {
    WorldUPSProtocol.UConnect uConnect = UMessageBuilder.createNewWorld(
      worldId,
      List.of()
    );
    GPBUtil.send(uConnect, outputStream);
  }

  public void sendPickUpRequests(
    @NonNull List<WorldUPSProtocol.UGoPickup> uGoPickup
  ) {
    //    this.seqNum++;
    WorldUPSProtocol.UCommands uCommands = UMessageBuilder.createUCommands(
      uGoPickup
    );
    GPBUtil.send(uCommands, outputStream);
  }
}
