package org.mini_amazon.services;

import org.mini_amazon.proto.WorldAmazonProtocol;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

  public void createNewWorld() {
    WorldAmazonProtocol.AConnect aConnect = WorldAmazonProtocol.AConnect.newBuilder()
            .build();
  }
}
