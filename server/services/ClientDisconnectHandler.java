package server.services;

import java.io.ObjectOutputStream;

import common.entities.UserStatus;
import server.entities.EventType;

/**
 * Clears the client's cache data off the server once they
 * disconnect.
 * <p>
 * Created on 2020.12.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClientDisconnectHandler implements Subscribable {
  public ClientDisconnectHandler() {

  }
  
  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.REMOVE_CLIENT_CONNECTION, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    String userId;
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      userId = GlobalServices.clientConnections.getUserId(toClient);
    } else {
      userId = (String) emitter;
    }

    GlobalServices.users.updateUserStatus(userId, UserStatus.OFFLINE);
    GlobalServices.clientConnections.remove(userId);
    GlobalServices.tokens.remove(userId);
  }
}
