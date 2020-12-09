package server.services;

import common.entities.payload.ClientInfoUpdate;
import server.entities.EventType;
import server.entities.FriendRequest;
import server.entities.User;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;
import server.resources.TempData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.2
 */
public class FriendRequestHandler implements Subscribable {
  public FriendRequestHandler() {

  }

  @Override
  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.FRIEND_REQUEST, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    FriendRequest friendReq = (FriendRequest)emitter;
    String recipientId = friendReq.getRecipientId();
    String userId = friendReq.getUserId();
    this.updateClient(userId);
    this.updateClient(recipientId);

  }

  private void updateClient(String userId) {
    if (TempData.clientConnections.hasClient(userId)) {
      User user = StoredData.users.getUser(userId);
     
      PayloadSender.send(
        TempData.clientConnections.getClient(userId), 
        new ClientInfoUpdate(
          1, 
          user.getStatus(),
          user.getFriends(),
          user.getIncomingFriendRequests(), 
          user.getOutgoingFriendRequests(),
          user.getBlocked(),
          user.getChannels() 
        )
      );
    }
  }
}
