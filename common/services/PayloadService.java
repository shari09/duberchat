package common.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import common.entities.payload.Payload;

public class PayloadService {
  public static Payload parse(String payloadString) {
    Payload payload = null;
    try {
      byte bytes[] = payloadString.getBytes();
      ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      payload = (Payload)objIn.readObject();
    } catch (Exception e) {
      System.out.println("Deserializing exception");
    }
    return payload;
  }

  public static String toString(Payload payload) {
    String serialized = null;
    try {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(payload);
      objOut.flush();
      serialized = byteOut.toString();
    } catch (Exception e) {
      System.out.println("Serializing exception");

    }
    return serialized;
  }   

}
