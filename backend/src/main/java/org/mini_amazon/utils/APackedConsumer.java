package org.mini_amazon.utils;

import org.mini_amazon.proto.WorldAmazonProtocol;

import java.util.function.Consumer;

public class APackedConsumer implements Consumer<WorldAmazonProtocol.APacked> {
  @Override
  public void accept(WorldAmazonProtocol.APacked aPacked) {
    System.out.println("APacked: " + aPacked);
  }
}
