package server.services;

import java.io.ObjectOutputStream;

import common.entities.payload.Payload;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

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
