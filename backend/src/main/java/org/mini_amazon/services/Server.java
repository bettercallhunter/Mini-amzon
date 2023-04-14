package org.mini_amazon.services;

import com.google.protobuf.CodedInputStream;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;


public class Server {
  //Server Communicate with World
  private Socket amazonToWarehouseSocket;
  private final String worldHost;
  private final int worldPort;

  public Server(String worldHost, int worldPort, int serverToClientPortNum, String serverToAmazonHost, int serverToAmazonPortNum) throws IOException {
    //Establish Connection with World
    this.worldHost = worldHost;
    this.worldPort = worldPort;
    this.amazonToWarehouseSocket = new Socket(this.worldHost, this.worldPort);
    OutputStream outputStream = this.amazonToWarehouseSocket.getOutputStream();
    System.out.println("Server: Connected to World");
  }

  public void sendAConnect(Long worldId, List<Pair<Integer, Integer>> positions) {

    // create all warehouses based on the positions
    WorldAmazonProtocol.AConnect.Builder aConnectBuilder = WorldAmazonProtocol.AConnect.newBuilder().setIsAmazon(true);
    if (worldId != null) {
      aConnectBuilder.setWorldid(worldId);
    } else {
      // store all warehouses in db
//      for (Pair<Integer, Integer> position : positions) {
//        WorldAmazonProtocol.AInitWarehouse.Builder warehouseBuilder = WorldAmazonProtocol.AInitWarehouse.newBuilder();
////        warehouseBuilder.setId(1L);
//        warehouseBuilder.setX(position.getFirst());
//        warehouseBuilder.setY(position.getSecond());
//        aConnectBuilder.addWarehouses(warehouseBuilder);
//      }


    }
    try {
      SocketClient.send(this.amazonToWarehouseSocket, aConnectBuilder.build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  // main
  public static void main(String[] args) {
    Server server = null;
    try {
      server = new Server("localhost", 23456, 8081, "localhost", 8082);
      server.sendAConnect(null, null);
      InputStream inputStream = server.amazonToWarehouseSocket.getInputStream();
      CodedInputStream codedInputStream = CodedInputStream.newInstance(inputStream);
      //int length = codedInputStream.readRawVarint32();
      WorldAmazonProtocol.AConnected aConnected = WorldAmazonProtocol.AConnected.parseFrom(codedInputStream.readByteArray());
//              auRequest = UpsAmazon.AURequest.parseFrom(codedInputStream.readByteArray());
      System.out.println(aConnected);
//      return aConnected.toBuilder();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
