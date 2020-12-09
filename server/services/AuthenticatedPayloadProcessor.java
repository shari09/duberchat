package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.ChangePassword;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.FriendRequest;
import server.entities.AuthenticatedClientRequest;
import server.entities.EventType;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun
 * @version 1.1.0
 * @since 1.0.1
 */
public class AuthenticatedPayloadProcessor implements Subscribable {
  private PriorityBlockingQueue<AuthenticatedClientRequest> payloadQueue;
  private boolean running;
  public AuthenticatedPayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    this.running = false;
  }

  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.AUTHENTICATED_PAYLOAD, this);
  }

  public void onEvent(Object newPayload) {
    this.payloadQueue.add((AuthenticatedClientRequest)newPayload);
    if (this.running) {
      return;
    }
    this.running = true;
    while (!this.payloadQueue.isEmpty()) {
      AuthenticatedClientRequest client = this.payloadQueue.poll();
      switch (client.getPayload().getType()) {
        case CHANGE_PASSWORD:
          this.changePassword(client);
          break;
        case CHANGE_CHANNEL:
          break;
        case CHANGE_PROFILE:
          break;
        case SEND_MESSAGE:
          this.sendMessage(client);
          break;
        case REQUEST_MESSAGES:
          break;
        case FRIEND_REQUEST:
          this.sendFriendRequest(client);
          break;
        case FRIEND_REQUEST_RESPONSE:
          break;
        case REQUEST_ATTACHMENT:
          break;
        default:
          System.out.println("Uh oh, an incorrect payload has ended up here");
          break;
      }

    }
    this.running = false;
  }

  private void changePassword(AuthenticatedClientRequest client) {
    ChangePassword payload = (ChangePassword)client.getPayload();
    boolean success = StoredData.users.changePassword(
      payload.getUserId(), 
      payload.getOriginalPassword(), 
      payload.getNewPassword()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, "Incorrect original password")
      );
      return;
    }
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, null)
    );
  }

  private void sendMessage(AuthenticatedClientRequest client) {

  }

  private void sendFriendRequest(AuthenticatedClientRequest client) {
    FriendRequest friendReq = (FriendRequest)client.getPayload();
    //sending request to a nonexistent user
    if (!StoredData.users.usernameExists(friendReq.getRecipientName())) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, "Recipient does not exist")
      );
      return;
    }
    StoredData.users.sendFriendRequest(
      friendReq.getUserId(), 
      friendReq.getRecipientName()
    );

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, null)  
    );
  }


}
