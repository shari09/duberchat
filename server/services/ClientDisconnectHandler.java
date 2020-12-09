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
<<<<<<< HEAD
 * @version 1.0.1
 * @since 1.0.1
=======
 * @version 1.0.0
 * @since 1.0.0
>>>>>>> 7a25c22a9ed78d1284104337731193e52c18bbbe
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
