package org.mini_amazon.utils;

import org.mini_amazon.proto.WorldAmazonProtocol;

import java.util.function.Consumer;

public class APurchaseMoreConsumer implements Consumer<WorldAmazonProtocol.APurchaseMore> {

  @Override
  public void accept(WorldAmazonProtocol.APurchaseMore message) {
    System.out.println("APurchaseMore: " + message);

  }
}
