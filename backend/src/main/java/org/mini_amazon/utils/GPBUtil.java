package org.mini_amazon.utils;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import org.mini_amazon.configs.Config;

import java.io.IOException;
import java.io.OutputStream;


public class GPBUtil {
  public static synchronized boolean send(Message message, OutputStream outputStream) {
    try {
      CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
      int size = message.getSerializedSize();
      codedOutputStream.writeUInt32NoTag(size);
      message.writeTo(codedOutputStream);
      if (Config.DEBUG) {
        System.out.println("send: " + message);
      }
      codedOutputStream.flush();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static synchronized <T extends Message.Builder> T receiveFrom(T response, CodedInputStream codedInputStream) throws IOException {
//    WorldAmazonProtocol.AResponses aResponses = WorldAmazonProtocol.AResponses.parseFrom(this.codedInputStream.readByteArray());
    response.mergeFrom(codedInputStream.readByteArray());
    if (Config.DEBUG) {
      System.out.println("receive: " + response);
    }
    return response;


  }
}
