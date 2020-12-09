package server.services;

import server.entities.Client;
import server.entities.EventType;
import server.resources.GlobalEventQueue;
import server.resources.TempData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
<<<<<<< HEAD
 * @version 1.0.1
 * @since 1.0.1
=======
 * @version 1.0.0
 * @since 1.0.0
>>>>>>> 7a25c22a9ed78d1284104337731193e52c18bbbe
 */
public class AuthenticatedClientHandler implements Subscribable {
  public AuthenticatedClientHandler() {
    
  }

  @Override
  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    Client client = (Client)emitter;
    TempData.clientConnections.add(client.getUserId(), client.getClient());
  }
}
