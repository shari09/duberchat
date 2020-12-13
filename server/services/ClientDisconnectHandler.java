package server.services;

import java.io.ObjectOutputStream;

import server.entities.EventType;

/**
 * [insert description]
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

  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      String userId = GlobalServices.clientConnections.getUserId(toClient);
      GlobalServices.clientConnections.remove(toClient);
      GlobalServices.tokens.remove(userId);
      return;
    }
    String userId = (String) emitter;
    GlobalServices.clientConnections.remove(userId);
    GlobalServices.tokens.remove(userId);
  }
}
