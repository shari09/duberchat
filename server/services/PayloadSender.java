package server.services;

import java.io.ObjectOutputStream;

import common.entities.payload.Payload;

public class PayloadSender {
  public static void send(ObjectOutputStream client, Payload payload) {
    synchronized(client) {
      try {
        client.writeObject(payload);
        client.flush();
      } catch (Exception e) {
        System.out.println("Failed to send payload to client");
        System.out.println(e.getMessage());
      }
    }
  }
}
