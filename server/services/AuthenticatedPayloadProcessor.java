package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Message;
import common.entities.payload.ChangePassword;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.FriendRequestResponse;
import common.entities.payload.FriendRequestToServer;
import common.entities.payload.MessageToServer;
import common.entities.payload.MessagesToClient;
import common.entities.payload.RequestMessages;
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
 * @version 1.0.0
 * @since 1.0.0
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

  @Override
  public void onEvent(Object newPayload, EventType eventType) {
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
        case MESSAGE_TO_SERVER:
          this.sendMessage(client);
          break;
        case REQUEST_MESSAGES:
          this.requestMessages(client);
          break;
        case FRIEND_REQUEST:
          this.sendFriendRequest(client);
          break;
        case FRIEND_REQUEST_RESPONSE:
          this.respondFriendRequest(client);
          break;
        case REQUEST_ATTACHMENT:
          break;
        case CREATE_CHANNEL:
          this.createChannel(client);
          break;
        case ADD_PARTICIPANTS_TO_CHANNEL:
          break;
        case BLOCK_USER:
          break;
        case REMOVE_PARTICIPANT:
          break;
        case REMOVE_MESSAGE:
          break;
        case EDIT_MESSAGE:
          break;
        case LEAVE_CHANNEL:
          break;
        case TRANSFER_OWNERSHIP:
          break;
        case BLACKLIST_PARTICIPANT:
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
        new ClientRequestStatus(1, payload.getId(), "Incorrect original password")
      );
      return;
    }
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void sendMessage(AuthenticatedClientRequest client) {
    MessageToServer payload = (MessageToServer)client.getPayload();
    boolean success = StoredData.channels.addMessage(
      payload.getUserId(), 
      payload.getChannelId(), 
      payload.getContent(), 
      payload.getAttachment(), 
      payload.getAttachmentName()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Message sending failure")
      );
      return;
    }
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void sendFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestToServer friendReq = (FriendRequestToServer)client.getPayload();
    //sending request to a nonexistent user
    if (!StoredData.users.usernameExist(friendReq.getRecipientName())) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, friendReq.getId(), "Recipient does not exist")
      );
      return;
    }
    StoredData.users.sendFriendRequest(
      friendReq.getUserId(), 
      friendReq.getRecipientName(),
      friendReq.getRequestMessage()
    );

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, friendReq.getId(), null)  
    );
  }

  private void respondFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestResponse response = (FriendRequestResponse)client.getPayload();
    boolean success;
    if (response.isAccepted()) {
      success = StoredData.users.acceptFriendRequest(
        response.getUserId(), 
        response.getRequesterId()
      );
    } else {
      success = StoredData.users.rejectFriendRequest(
        response.getUserId(), 
        response.getRequesterId()
      );
    }
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, response.getId(), "Error responding to friend request")  
      );
    }


    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, response.getId(), null)  
    );
  }

  private void createChannel(AuthenticatedClientRequest client) {
    
  }

  private void requestMessages(AuthenticatedClientRequest client) {
    RequestMessages req = (RequestMessages)client.getPayload();
    Message[] msgs = StoredData.channels.getMessages(
      req.getChannelId(), 
      req.getCreated(), 
      req.getQuantity()
    );

    if (msgs == null) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, req.getId(), "Error retrieving messages")  
      );
      return;
    }
    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, req.getId(), null)  
    );

    PayloadSender.send(
      client.getClientOut(),
      new MessagesToClient(1, req.getChannelId(), msgs)
    );

  }

}
