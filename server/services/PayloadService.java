package server.services;

import java.io.ObjectOutputStream;
import java.net.SocketException;

import common.entities.payload.Payload;
import common.entities.payload.client_to_server.AuthenticatablePayload;
import common.entities.payload.server_to_client.ClientRequestStatus;
import server.entities.AuthenticatedClientRequest;
import server.entities.ClientRequest;
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

public class PayloadService {
  public static void send(ObjectOutputStream client, Payload payload) {
    synchronized (client) {
      try {
        client.reset();
        client.writeObject(payload);
        String userId = GlobalServices.clientConnections.getUserId(client);
        String msg = "";
        if (userId != null) {
          msg = "to user:" + userId;
        }
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          String.format(
            "[SUCCESS] Sending %s payload %s", 
            payload.getType(), 
            msg
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
            "[ERROR] Sending %s payload to %s", 
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

    synchronized (client) {
      try {
        client.reset();
        client.writeObject(payload);
        client.flush();
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          String.format(
            "[SUCCESS] Sending %s payload to %s", 
            payload.getType(), 
            userId
          )
        );
      } catch (Exception e) {

      }
    }
  }

  /**
   * 
   * @param client
   * @param success
   * @param logSuccess
   * @param logError
   * @param errorMsgToClient
   */
  public static void sendAuthenticatedResponse(
    AuthenticatedClientRequest client,
    boolean success,
    String log,
    String errorMsgToClient
  ) {
    AuthenticatablePayload payload = (AuthenticatablePayload)client.getPayload();
    String username = GlobalServices.users.getUsername(payload.getUserId());
    if (success) {
      log(String.format("[SUCCESS] %s:%s: %s", username, payload.getUserId(), log));
      sendSuccess(client);
    } else {
      sendError(client, errorMsgToClient);
      log(String.format("[ERROR] %s:%s: %s", username, payload.getUserId(), log));
    }
    
  }

  /**
   * 
   * @param client
   * @param success
   * @param logSuccess
   * @param logError
   * @param errorMsgToClient
   */
  public static void sendResponse(
    ClientRequest client,
    boolean success,
    String log,
    String errorMsgToClient
  ) {
    if (success) {
      log(String.format("[SUCCESS] %s", log));
      sendSuccess(client);
    } else {
      sendError(client, errorMsgToClient);
      log(String.format("[ERROR] %s", log));
    }
    
  }


  public static void sendSuccess(ClientRequest client) {
    PayloadService.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, client.getPayload().getId(), null)
    );
  }

  public static void sendError(ClientRequest client, String errorMsg) {
    PayloadService.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, client.getPayload().getId(), errorMsg)
    );
  }

  public static void log(String msg) {
    GlobalServices.serverEventQueue.emitEvent(EventType.NEW_LOG, 1, msg);
  }
}
