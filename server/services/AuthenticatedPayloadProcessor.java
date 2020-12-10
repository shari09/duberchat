package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Attachment;
import common.entities.Message;
import common.entities.payload.AddParticipant;
import common.entities.payload.AttachmentToClient;
import common.entities.payload.BlacklistUser;
import common.entities.payload.BlockUser;
import common.entities.payload.ChangeChannel;
import common.entities.payload.ChangePassword;
import common.entities.payload.ChangeProfile;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.CreateChannel;
import common.entities.payload.EditMessage;
import common.entities.payload.FriendRequestResponse;
import common.entities.payload.FriendRequestToServer;
import common.entities.payload.LeaveChannel;
import common.entities.payload.MessageToServer;
import common.entities.payload.MessagesToClient;
import common.entities.payload.RemoveMessage;
import common.entities.payload.RemoveParticipant;
import common.entities.payload.RequestAttachment;
import common.entities.payload.RequestMessages;
import common.entities.payload.TransferOwnership;
import common.entities.payload.UpdateStatus;
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
    this.payloadQueue.add((AuthenticatedClientRequest) newPayload);
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
          this.changeChannel(client);
          break;
        case CHANGE_PROFILE:
          this.changeProfile(client);
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
          this.requestAttachment(client);
          break;
        case CREATE_CHANNEL:
          this.createChannel(client);
          break;
        case ADD_PARTICIPANT:
          this.addParticipant(client);
          break;
        case BLOCK_USER:
          this.blockUser(client);
          break;
        case REMOVE_PARTICIPANT:
          this.removeParticipant(client);
          break;
        case REMOVE_MESSAGE:
          this.removeMessage(client);
          break;
        case EDIT_MESSAGE:
          this.editMessage(client);
          break;
        case LEAVE_CHANNEL:
          this.leaveChannel(client);
          break;
        case TRANSFER_OWNERSHIP:
          this.transferOwnership(client);
          break;
        case BLACKLIST_USER:
          this.blacklistUser(client);
          break;
        case UPDATE_STATUS:
          this.updateStatus(client);
          break;
        default:
          System.out.println("Uh oh, an incorrect payload has ended up here");
          break;
      }

    }
    this.running = false;
  }

  private void transferOwnership(AuthenticatedClientRequest client) {
    TransferOwnership payload = (TransferOwnership)client.getPayload();
    boolean success = StoredData.channels.transferOwnership(
      payload.getRecipientId(), 
      payload.getChannelId()
    );

    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Transfer failed")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void leaveChannel(AuthenticatedClientRequest client) {
    LeaveChannel payload = (LeaveChannel)client.getPayload();
    boolean success = StoredData.channels.leaveChannel(
      payload.getUserId(), 
      payload.getChannelId()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Error leaving channel")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void changeChannel(AuthenticatedClientRequest client) {
    ChangeChannel payload = (ChangeChannel)client.getPayload();
    boolean success = StoredData.channels.changeChannelSettings(
      payload.getChannelId(),
      payload.getFieldToChange(),
      payload.getNewValue()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Error changing channel settings")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }


  private void changeProfile(AuthenticatedClientRequest client) {
    ChangeProfile payload = (ChangeProfile)client.getPayload();
    StoredData.users.changeProfile(
      payload.getUserId(), 
      payload.getFieldToChange(), 
      payload.getNewValue()
    );

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void updateStatus(AuthenticatedClientRequest client) {
    UpdateStatus payload = (UpdateStatus)client.getPayload();
    StoredData.users.updateUserStatus(
      payload.getUserId(), 
      payload.getStatus()
    );
    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }



  private void requestAttachment(AuthenticatedClientRequest client) {
    RequestAttachment payload = (RequestAttachment)client.getPayload();
    Attachment attachment = StoredData.channels.getAttachment(payload.getAttachmentId());
    if (attachment == null) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Unable to download attachment")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(),
      new ClientRequestStatus(1, payload.getId(), null)
    );

    PayloadSender.send(
      client.getClientOut(), 
      new AttachmentToClient(1, attachment)
    );
  }

  private void removeParticipant(AuthenticatedClientRequest client) {
    RemoveParticipant payload = (RemoveParticipant)client.getPayload();
    StoredData.channels.removeParticipant(
      payload.getParticipantId(), 
      payload.getChannelId()
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void blacklistUser(AuthenticatedClientRequest client) {
    BlacklistUser payload = (BlacklistUser)client.getPayload();
    StoredData.channels.blacklistUser(
      payload.getParticipantId(), 
      payload.getChannelId()
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void addParticipant(AuthenticatedClientRequest client) {
    AddParticipant payload = (AddParticipant)client.getPayload();
    if (StoredData.users.isFriend(payload.getUserId(), payload.getParticipantId())) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, 
          payload.getId(), 
          "The user you are trying to add is not your friend"
        )
      );
      return;
    }
    boolean success = StoredData.channels.addParticipant(
      payload.getParticipantId(), 
      payload.getChannelId()
    );

    if (!success) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Participant blacklisted")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void blockUser(AuthenticatedClientRequest client) {
    BlockUser payload = (BlockUser) client.getPayload();
    String toBeBlockedId = StoredData.users.getUserId(payload.getBlockUsername());

    boolean success = StoredData.users.blockUser(payload.getUserId(), toBeBlockedId);
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, 
          payload.getId(), 
          "The user you are trying to block does not exist"
        )
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void editMessage(AuthenticatedClientRequest client) {
    EditMessage payload = (EditMessage) client.getPayload();
    StoredData.channels.editMessage(
      payload.getChannelId(), 
      payload.getMessageId(), 
      payload.getNewContent()
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void removeMessage(AuthenticatedClientRequest client) {
    RemoveMessage payload = (RemoveMessage) client.getPayload();
    StoredData.channels.removeMessage(
      payload.getChannelId(), 
      payload.getMessageId()
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void changePassword(AuthenticatedClientRequest client) {
    ChangePassword payload = (ChangePassword) client.getPayload();
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
    PayloadSender.send(client.getClientOut(), new ClientRequestStatus(1, payload.getId(), null));
  }

  private void sendMessage(AuthenticatedClientRequest client) {
    MessageToServer payload = (MessageToServer) client.getPayload();
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
    FriendRequestToServer friendReq = (FriendRequestToServer) client.getPayload();
    String recipientId = StoredData.users.getUserId(friendReq.getRecipientName());
    if (StoredData.users.isBlocked(recipientId, friendReq.getId())) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, friendReq.getId(), "You have been blocked")
      );
      return;
    }

    boolean success = StoredData.users.sendFriendRequest(
      friendReq.getUserId(), 
      recipientId,
      friendReq.getRequestMessage()
    );

    // sending request to a nonexistent user
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, friendReq.getId(), "Recipient does not exist")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, friendReq.getId(), null)
    );
  }

  private void respondFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestResponse response = (FriendRequestResponse) client.getPayload();
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

  // TODO: make sure the owner/invited user aren't blocked
  private void createChannel(AuthenticatedClientRequest client) {
    CreateChannel payload = (CreateChannel) client.getPayload();
    StoredData.channels.createGroupChannel(
      payload.getParticipants(), 
      payload.getName(), 
      payload.getUserId()
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void requestMessages(AuthenticatedClientRequest client) {
    RequestMessages payload = (RequestMessages) client.getPayload();
    Message[] msgs = StoredData.channels.getMessages(
      payload.getChannelId(), 
      payload.getCreated(), 
      payload.getQuantity()
    );

    if (msgs == null) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Error retrieving messages")
      );
      return;
    }
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

    PayloadSender.send(
      client.getClientOut(), 
      new MessagesToClient(1, payload.getChannelId(), msgs)
    );

  }

}
