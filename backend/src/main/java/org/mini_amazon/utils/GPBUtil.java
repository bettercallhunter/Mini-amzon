package org.mini_amazon.utils;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import org.mini_amazon.configs.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class GPBUtil {
  public static synchronized boolean send(Message message, OutputStream outputStream) {
    try {
      CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
//      System.out.println("send: " + message);
      int size = message.getSerializedSize();
      codedOutputStream.writeUInt32NoTag(size);
      message.writeTo(codedOutputStream);
      if (Config.DEBUG) {
        System.out.println("send: [" + message + "]");
      }
      codedOutputStream.flush();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("send: [" + message + "]");
      return false;
    }
  }

  public static <T extends Message.Builder> boolean receiveFrom(T response, InputStream in) {
    try {
      CodedInputStream codedInputStream = CodedInputStream.newInstance(in);
      response.mergeFrom(codedInputStream.readByteArray());

      if (Config.DEBUG) {
        System.out.println("recv: [" + response + "]");
      }
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("recv: [" + response + "]");
      return false;
    }
  }

//  public static synchronized <T extends Message.Builder> T receiveFrom(T response, CodedInputStream codedInputStream) throws IOException {
//    response.mergeFrom(codedInputStream.readByteArray());
//    if (Config.DEBUG) {
//      System.out.println("receive: {" + response + "}");
//    }
//    return response;
//  }
}
