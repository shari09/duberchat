package server.services;

import common.entities.payload.ClientFriendsUpdate;
import server.entities.EventType;
import server.entities.User;
import server.resources.GlobalEventQueue;
import server.resources.TempData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class FriendInfoUpdater implements Subscribable {
  public FriendInfoUpdater() {

  }

  @Override
  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.FRIEND_UPDATE, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    User user = (User)emitter;
    PayloadSender.send(
      TempData.clientConnections.getClient(user.getId()), 
      new ClientFriendsUpdate(
        1, 
        user.getFriends(),
        user.getIncomingFriendRequests(), 
        user.getOutgoingFriendRequests()
      )
    );

  }
}
