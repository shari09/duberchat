package server.services;

import java.io.ObjectOutputStream;
import java.net.SocketException;

import common.entities.payload.Payload;
import server.entities.EventType;
import server.resources.GlobalEventQueue;
import server.resources.TempData;

/**
 * A static method that sends a given payload to a given client (output stream).
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
      } catch (SocketException e) {
        GlobalEventQueue.queue.emitEvent(EventType.CLIENT_DISCONNECTED, 2, client);
      } catch (Exception e) {
        System.out.println("Failed to send payload to client");
        e.printStackTrace();
      }
    }
  }

  public static void send(String userId, Payload payload) {
    if (!TempData.clientConnections.hasClient(userId)) {
      return;
    }
    ObjectOutputStream client = TempData.clientConnections.getClient(userId);
    synchronized(client) {
      try {
        client.writeObject(payload);
        client.flush();
      } catch (SocketException e) {
        GlobalEventQueue.queue.emitEvent(EventType.CLIENT_DISCONNECTED, 2, client);
      } catch (Exception e) {
        System.out.println("Failed to send payload to client");
        e.printStackTrace();
      }
    }
  }
}
