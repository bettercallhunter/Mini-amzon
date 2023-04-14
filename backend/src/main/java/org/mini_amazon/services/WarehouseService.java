package org.mini_amazon.services;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {
  public static class AProduct {
    long id;
    String description;
    int count;
  }

  public static WorldAmazonProtocol.AConnect createNewWorld(Long worldId, List<Pair<Integer, Integer>> positions) {
    WorldAmazonProtocol.AConnect.Builder aConnect = WorldAmazonProtocol.AConnect.newBuilder().setIsAmazon(true).setWorldid(worldId);
    for (Pair<Integer, Integer> position : positions) {
      WorldAmazonProtocol.AInitWarehouse.Builder warehouseBuilder = WorldAmazonProtocol.AInitWarehouse.newBuilder();
      warehouseBuilder.setX(position.getFirst());
      warehouseBuilder.setY(position.getSecond());
      aConnect.addInitwh(warehouseBuilder);
    }
    return aConnect.build();
  }

  public static WorldAmazonProtocol.APack createAPack(int warehouseId, List<AProduct> products, long shipId, long seqNum) {
    WorldAmazonProtocol.APack.Builder aPackBuilder = WorldAmazonProtocol.APack.newBuilder();
    aPackBuilder.setWhnum(warehouseId);
    aPackBuilder.setShipid(shipId);
    aPackBuilder.setSeqnum(seqNum);
    for (AProduct product : products) {
      WorldAmazonProtocol.AProduct aProduct = createAProduct(product);
      aPackBuilder.addThings(aProduct);
    }
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

  public static WorldAmazonProtocol.APurchaseMore createAPurchaseMore(int warehouseId, List<AProduct> products, long seqNum) {
    WorldAmazonProtocol.APurchaseMore.Builder aPurchaseMoreBuilder = WorldAmazonProtocol.APurchaseMore.newBuilder();
    aPurchaseMoreBuilder.setWhnum(warehouseId);
    aPurchaseMoreBuilder.setSeqnum(seqNum);
    for (AProduct product : products) {
      WorldAmazonProtocol.AProduct aProduct = createAProduct(product);
      aPurchaseMoreBuilder.addThings(aProduct);
    }
    return aPurchaseMoreBuilder.build();
  }

  private static WorldAmazonProtocol.AProduct createAProduct(AProduct product) {
    WorldAmazonProtocol.AProduct.Builder aProductBuilder = WorldAmazonProtocol.AProduct.newBuilder();
    aProductBuilder.setId(product.id);
    aProductBuilder.setDescription(product.description);
    aProductBuilder.setCount(product.count);
    return aProductBuilder.build();
  }


}
