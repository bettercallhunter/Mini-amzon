package org.mini_amazon.utils;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AMessageBuilder {

  // if worldId is null, then it is a new world
  public static WorldAmazonProtocol.AConnect createNewWorld(Long worldId, List<WorldAmazonProtocol.AInitWarehouse> wareHouses) {

    WorldAmazonProtocol.AConnect.Builder aConnect = WorldAmazonProtocol.AConnect.newBuilder().setIsAmazon(true);
    if (worldId != null) {
      aConnect.setWorldid(worldId);
    }

    aConnect.addAllInitwh(wareHouses);
    return aConnect.build();
  }

  public static WorldAmazonProtocol.APack createAPack(int warehouseId, List<WorldAmazonProtocol.AProduct> products, long shipId, long seqNum) {
    WorldAmazonProtocol.APack.Builder aPackBuilder = WorldAmazonProtocol.APack.newBuilder();
    aPackBuilder.setWhnum(warehouseId);
    aPackBuilder.setShipid(shipId);
    aPackBuilder.setSeqnum(seqNum);
    aPackBuilder.addAllThings(products);
    return aPackBuilder.build();
  }

  public static WorldAmazonProtocol.APutOnTruck createAPutOnTruck(int warehouseId, int truckId, long shipId, long seqNum) {
    WorldAmazonProtocol.APutOnTruck.Builder aPutOnTruckBuilder = WorldAmazonProtocol.APutOnTruck.newBuilder();
    aPutOnTruckBuilder.setWhnum(warehouseId);
    aPutOnTruckBuilder.setShipid(shipId);
    aPutOnTruckBuilder.setSeqnum(seqNum);
    aPutOnTruckBuilder.setTruckid(truckId);
    return aPutOnTruckBuilder.build();
  }

  public static WorldAmazonProtocol.AQuery createAQuery(long packageId, long seqNum) {
    WorldAmazonProtocol.AQuery.Builder aQueryBuilder = WorldAmazonProtocol.AQuery.newBuilder();
    aQueryBuilder.setPackageid(packageId);
    aQueryBuilder.setSeqnum(seqNum);
    return aQueryBuilder.build();
  }

  public static WorldAmazonProtocol.APurchaseMore createAPurchaseMore(int warehouseId, List<WorldAmazonProtocol.AProduct> products, long seqNum) {
    WorldAmazonProtocol.APurchaseMore.Builder aPurchaseMoreBuilder = WorldAmazonProtocol.APurchaseMore.newBuilder();
    aPurchaseMoreBuilder.setWhnum(warehouseId);
    aPurchaseMoreBuilder.setSeqnum(seqNum);
    aPurchaseMoreBuilder.addAllThings(products);
    return aPurchaseMoreBuilder.build();
  }

  private static WorldAmazonProtocol.AProduct createAProduct(long id, String description, int count) {
    WorldAmazonProtocol.AProduct.Builder aProductBuilder = WorldAmazonProtocol.AProduct.newBuilder();
    aProductBuilder.setId(id);
    aProductBuilder.setDescription(description);
    aProductBuilder.setCount(count);
    return aProductBuilder.build();
  }

  public static WorldAmazonProtocol.ACommands createACommands(@NonNull List<WorldAmazonProtocol.APurchaseMore> aPurchaseMores, @NonNull List<WorldAmazonProtocol.APack> aPacks, @NonNull List<WorldAmazonProtocol.APutOnTruck> aPutOnTrucks, @NonNull List<WorldAmazonProtocol.AQuery> aQueries, int simspeed, boolean disconnect, List<Long> acks) {
    WorldAmazonProtocol.ACommands.Builder aCommandsBuilder = WorldAmazonProtocol.ACommands.newBuilder();
    aCommandsBuilder.setSimspeed(simspeed);
    aCommandsBuilder.setDisconnect(disconnect);
    aCommandsBuilder.addAllBuy(aPurchaseMores);
    aCommandsBuilder.addAllTopack(aPacks);
    aCommandsBuilder.addAllLoad(aPutOnTrucks);
    aCommandsBuilder.addAllQueries(aQueries);
    aCommandsBuilder.addAllAcks(acks);
    return aCommandsBuilder.build();
  }

  public static WorldAmazonProtocol.ACommands createACommands(@NonNull List<WorldAmazonProtocol.APurchaseMore> aPurchaseMores, @NonNull List<WorldAmazonProtocol.APack> aPacks, @NonNull List<WorldAmazonProtocol.APutOnTruck> aPutOnTrucks, @NonNull List<WorldAmazonProtocol.AQuery> aQueries, @NonNull List<Long> acks) {
    return createACommands(aPurchaseMores, aPacks, aPutOnTrucks, aQueries, 100, false, acks);
  }

  public static WorldAmazonProtocol.ACommands createACommands(@NonNull List<Long> acks) {
    return createACommands(List.of(), List.of(), List.of(), List.of(), acks);
  }


}
