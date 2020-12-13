package server.services;

import java.io.ObjectOutputStream;
import java.net.SocketException;

import common.entities.payload.ClientChannelsUpdate;
import common.entities.payload.Payload;
import server.entities.EventType;

/**
 * A static method that sends a given payload to a given client (output stream).
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class PayloadSender {
  public static void send(ObjectOutputStream client, Payload payload) {
    synchronized (client) {
      try {
        client.writeObject(payload);
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          String.format(
            "Sent %s payload to %s", 
            payload.getType(), 
            GlobalServices.clientConnections.getUserId(client)
          )
        );

        client.flush();
      } catch (SocketException e) {
        GlobalServices.serverEventQueue.emitEvent(EventType.CLIENT_DISCONNECTED, 2, client);
      } catch (Exception e) {
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          String.format(
            "Failed to send %s payload to %s", 
            payload.getType(), 
            GlobalServices.clientConnections.getUserId(client)
          )
        );
        e.printStackTrace();
      }
    }
  }

  public static void send(String userId, Payload payload) {
    if (!GlobalServices.clientConnections.hasClient(userId)) {
      return;
    }
    ObjectOutputStream client = GlobalServices.clientConnections.getClient(userId);
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format("Sent %s payload to %s", payload.getType(), userId)
    );
    synchronized (client) {
      try {
        client.writeObject(payload);
        client.flush();
      } catch (SocketException e) {
        GlobalServices.serverEventQueue.emitEvent(
          EventType.CLIENT_DISCONNECTED, 2, client
        );
      } catch (Exception e) {
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          String.format(
            "Failed to sent %s payload to %s", 
            payload.getType(), 
            userId
          )
        );
        e.printStackTrace();
      }
    }
  }
}
