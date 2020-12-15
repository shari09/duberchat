package server.services;

import common.entities.UserStatus;
import server.entities.Client;
import server.entities.EventType;

/**
 * Once a client is authenticated, they will be given a token
 * and their socket output stream info will be stored in
 * {@link GlobalServices}.
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see ClientConnections
 * @see TokensMap
 */
public class AuthenticatedClientHandler implements Subscribable {
  public AuthenticatedClientHandler() {
    
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    Client client = (Client)emitter;
    GlobalServices.clientConnections.add(
      client.getUserId(), 
      client.getClient(),
      client.getSocket()
    );
    GlobalServices.users.updateUserStatus(client.getUserId(), UserStatus.ACTIVE);
  }
}
