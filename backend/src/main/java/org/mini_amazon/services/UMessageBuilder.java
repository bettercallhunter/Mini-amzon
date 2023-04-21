package org.mini_amazon.services;

import java.util.List;
import org.mini_amazon.proto.WorldUPSProtocol;
import org.springframework.lang.NonNull;

public class UMessageBuilder {

  public static WorldUPSProtocol.UConnect createNewWorld(
    Long worldId,
    List<WorldUPSProtocol.UInitTruck> trucks
  ) {
    WorldUPSProtocol.UConnect.Builder uConnect = WorldUPSProtocol.UConnect
      .newBuilder()
      .setIsAmazon(false);
    if (worldId != null) {
      uConnect.setWorldid(worldId);
    }

    uConnect.addAllTrucks(trucks);
    return uConnect.build();
  }

  public static WorldUPSProtocol.UCommands createUCommands(
    @NonNull List<WorldUPSProtocol.UGoPickup> uGoPickup
  ) {
    WorldUPSProtocol.UCommands.Builder uCommandsBuilder = WorldUPSProtocol.UCommands.newBuilder();

    return uCommandsBuilder.build();
  }
}
