package server.services;

import java.io.ObjectOutputStream;

import server.entities.EventType;
import server.resources.GlobalEventQueue;
import server.resources.TempData;

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
    GlobalEventQueue.queue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream)emitter;
      String userId = TempData.clientConnections.getUserId(toClient);
      TempData.clientConnections.remove(toClient);
      TempData.tokens.remove(userId);
      return;
    }
    String userId = (String)emitter;
    TempData.clientConnections.remove(userId);
    TempData.tokens.remove(userId);
  }
}
