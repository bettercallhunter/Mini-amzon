package org.mini_amazon.utils;

import org.mini_amazon.proto.WorldAmazonProtocol;

import java.util.function.Consumer;

public class APackageConsumer implements Consumer<WorldAmazonProtocol.APackage> {
  @Override
  public void accept(WorldAmazonProtocol.APackage aPackage) {
    System.out.println("APackage: " + aPackage);
  }
}
