package server.services;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import common.entities.payload.Payload;
import common.entities.payload.client_to_server.AuthenticatablePayload;
import common.entities.payload.server_to_client.ClientRequestStatus;
import server.entities.AuthenticatedClientRequest;
import server.entities.ClientRequest;
import server.entities.EventType;
import server.entities.Log;
import server.entities.LogType;
import server.gui.LogsPanel;

/**
 * A set of static methods used for communication between server/client
 * and logging.
 * <p>
 * Created on 2020.12.06.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class CommunicationService {
  /**
   * Sends a payload to a client given the client's socket output stream.
   * @param client      the client's socket output stream
   * @param payload     the payload being sent
   */
  public static void send(ObjectOutputStream client, Payload payload) {
    synchronized (client) {
      try {
        client.reset();
        client.writeObject(payload);
        String userId = GlobalServices.clientConnections.getUserId(client);
        String msg = "";
        if (userId != null) {
          msg = String.format(
            "to user %s:%s",
            GlobalServices.users.getUsername(userId),
            userId
          );
        }
        //log
        CommunicationService.log(String.format(
          "Sending %s payload %s",
          payload.getType(),
          msg
        ), LogType.SUCCESS);

        client.flush();
      } catch (Exception e) {

      }
    }
  }

  /**
   * Sends a payload to a client given the client's user ID.
   * @param userId      the client's user ID
   * @param payload     the payload being sent
   */
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
        String msg = "";
        if (userId != null) {
          msg = String.format("to user %s:%s", GlobalServices.users.getUsername(userId), userId);
        }
        //log
        CommunicationService.log(String.format(
          "Sending %s payload %s",
          payload.getType(),
          msg
        ), LogType.SUCCESS);
      } catch (Exception e) {

      }
    }
  }

  /**
   * Sends the response to an {@link AuthenticatibleClientRequest}.
   * @param client             the authenticated client request
   * @param success            if the client request was a success
   * @param log                log to the logging panel of this operation
   * @param errorMsgToClient   the error message to client if the request errored
   * @see                      AuthenticatedPayloadProcessor
   * @see                      AuthenticatedPayload
   * @see                      LogsPanel
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
      log(String.format("%s:%s: %s", username, payload.getUserId(), log), LogType.SUCCESS);
      sendSuccess(client);
    } else {
      sendError(client, errorMsgToClient);
      log(String.format("%s:%s: %s", username, payload.getUserId(), log), LogType.CLIENT_ERROR);
    }

  }

  /**
   * Sends the response to an {@link ClientRequest}.
   * @param client             the authenticated client request
   * @param success            if the client request was a success
   * @param log                log to the logging panel of this operation
   * @param errorMsgToClient   the error message to client if the request errored
   * @see                      PayloadProcessor
   * @see                      LogsPanel
   */
  public static void sendResponse(
    ClientRequest client,
    boolean success,
    String log,
    String errorMsgToClient
  ) {
    if (success) {
      log(String.format("%s", log), LogType.SUCCESS);
      sendSuccess(client);
    } else {
      sendError(client, errorMsgToClient);
      log(String.format("%s", log), LogType.CLIENT_ERROR);
    }

  }


  /**
   * Sends a success request response to the client.
   * @param client   the client's socket output stream
   */
  public static void sendSuccess(ClientRequest client) {
    CommunicationService.send(
      client.getClientOut(),
      new ClientRequestStatus(1, client.getPayload().getId(), null)
    );
  }


  /**
   * Sends a failed request response to the client.
   * @param client   the client's socket output stream
   */
  public static void sendError(ClientRequest client, String errorMsg) {
    CommunicationService.send(
      client.getClientOut(),
      new ClientRequestStatus(1, client.getPayload().getId(), errorMsg)
    );
  }

  /**
   * Logs something to the log panel.
   * @param msg     the message to log
   * @param type    the type of log this is
   * @see           LogsPanel
   */
  public static void log(String msg, LogType type) {
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 1, new Log(msg, type)
    );
  }

  /**
   * Gets the stack trace as a string value.
   * @param e   the exception
   * @return    the stack trace
   */
  public static String getStackTrace(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }
}
