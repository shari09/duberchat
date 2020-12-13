package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Attachment;
import common.entities.Message;
import common.entities.payload.AddParticipant;
import common.entities.payload.AttachmentToClient;
import common.entities.payload.AuthenticatablePayload;
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
  // private boolean running;

  public AuthenticatedPayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    // this.running = false;
  }

  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_PAYLOAD, this);
  }

  @Override
  public void onEvent(Object newPayload, EventType eventType) {
    this.payloadQueue.add((AuthenticatedClientRequest) newPayload);
    //log
    AuthenticatablePayload payload = ((AuthenticatedClientRequest)newPayload).getPayload();
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Received authenticated payload:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getType()
      )
    );
    // if (this.running) {
    // return;
    // }
    // this.running = true;
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
          //log
          GlobalServices.serverEventQueue.emitEvent(
            EventType.NEW_LOG, 
            1,
            String.format(
              "Incorrect payload: %s", 
              client.getPayload().getType()
            )
          );
          break;
      }

    }
    // this.running = false;
  }

  private void transferOwnership(AuthenticatedClientRequest client) {
    TransferOwnership payload = (TransferOwnership) client.getPayload();
    boolean success = GlobalServices.channels.transferOwnership(
      payload.getRecipientId(), 
      payload.getChannelId()
    );

    if (!success) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Transfer failed")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Ownership transfer failed",
          this.getUsername(payload), 
          payload.getType()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Ownership transfer succeeded", 
        this.getUsername(payload),
        payload.getUserId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void leaveChannel(AuthenticatedClientRequest client) {
    LeaveChannel payload = (LeaveChannel) client.getPayload();
    boolean success = GlobalServices.channels.leaveChannel(
      payload.getUserId(), 
      payload.getChannelId()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Error leaving channel")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error leaving channel", 
          this.getUsername(payload),
          payload.getUserId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Left channel %s", 
        this.getUsername(payload),
        GlobalServices.users.getUsername(payload.getUserId()),
        payload.getUserId(),
        payload.getChannelId()
      )
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void changeChannel(AuthenticatedClientRequest client) {
    ChangeChannel payload = (ChangeChannel) client.getPayload();
    boolean success = GlobalServices.channels.changeChannelSettings(
      payload.getChannelId(),
      payload.getFieldToChange(), 
      payload.getNewValue()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, payload.getId(), "Error changing channel settings"
        )
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error changing channel:%s settings", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getChannelId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Changed channel:%s settings", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getChannelId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }


  private void changeProfile(AuthenticatedClientRequest client) {
    ChangeProfile payload = (ChangeProfile) client.getPayload();

    boolean success = false;
    switch (payload.getFieldToChange()) {
      case DESCRIPTION:
        success = GlobalServices.users.changeDescription(
          payload.getUserId(), 
          payload.getNewValue()
        );
        break;
      case USERNAME:
        success = GlobalServices.users.changeUsername(
          payload.getUserId(), 
          payload.getNewValue()
        );
        break;
    }

    if (!success) {
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to change %s ",
          this.getUsername(payload),
          payload.getUserId(),
          payload.getFieldToChange()
        )
      );

      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Change failed")
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Changed %s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getFieldToChange()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void updateStatus(AuthenticatedClientRequest client) {
    UpdateStatus payload = (UpdateStatus) client.getPayload();
    GlobalServices.users.updateUserStatus(
      payload.getUserId(), 
      payload.getStatus()
    );
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Changed %s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getStatus()
      )
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void requestAttachment(AuthenticatedClientRequest client) {
    RequestAttachment payload = (RequestAttachment) client.getPayload();
    Attachment attachment = GlobalServices.channels.getAttachment(payload.getAttachmentId());
    if (attachment == null) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, payload.getId(), "Unable to download attachment"
        )
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to download attachment:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getAttachmentId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Downloaded attachment:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getAttachmentId()
      )
    );
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
    RemoveParticipant payload = (RemoveParticipant) client.getPayload();
    boolean success = GlobalServices.channels.removeParticipant(
      payload.getParticipantId(),
      payload.getChannelId()
    );

    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Cannot remove participant")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error removing participant:%s from channel:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getParticipantId(),
          payload.getChannelId()
        )
      );
      return;
    }
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Removed participant:%s from channel:%s",
        this.getUsername(payload), 
        payload.getUserId(),
        payload.getParticipantId(),
        payload.getChannelId()
      )
    );


    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void blacklistUser(AuthenticatedClientRequest client) {
    BlacklistUser payload = (BlacklistUser) client.getPayload();
    boolean success = GlobalServices.channels.blacklistUser(
      payload.getParticipantId(), 
      payload.getChannelId()
    );

    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Cannot blacklist participant")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error blacklisting participant:%s from channel:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getParticipantId(),
          payload.getChannelId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Blacklisted participant:%s from channel:%s",
        this.getUsername(payload), 
        payload.getUserId(),
        payload.getParticipantId(),
        payload.getChannelId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void addParticipant(AuthenticatedClientRequest client) {
    AddParticipant payload = (AddParticipant) client.getPayload();
    if (
      !GlobalServices.users.isFriend( //not friend
        payload.getUserId(), 
        payload.getParticipantId()
      ) 
      || 
      !GlobalServices.channels.addParticipant( //blacklisted
        payload.getParticipantId(), 
        payload.getChannelId()
      )
    ) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Error adding user")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error adding %s:%s to channel:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getParticipantId(),
          payload.getChannelId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Added %s:%s to channel:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getParticipantId(),
        payload.getChannelId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void blockUser(AuthenticatedClientRequest client) {
    BlockUser payload = (BlockUser) client.getPayload();
    String toBeBlockedId = GlobalServices.users.getUserId(
      payload.getBlockUsername()
    );

    boolean success = GlobalServices.users.blockUser(
      payload.getUserId(), 
      toBeBlockedId
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, 
          payload.getId(), 
          "Invalid user"
        )
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to block %s:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getBlockUsername()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Blocked %s:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getBlockUsername()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

  }

  private void editMessage(AuthenticatedClientRequest client) {
    EditMessage payload = (EditMessage) client.getPayload();
    if (
      !GlobalServices.channels.isMessageSender(
        payload.getUserId(), 
        payload.getChannelId(),
        payload.getMessageId()
      )
    ) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Cannot edit message")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to edit msg:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getMessageId()
        )
      );
      return;
    }
    GlobalServices.channels.editMessage(
      payload.getChannelId(), 
      payload.getMessageId(), 
      payload.getNewContent()
    );

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Edited msg:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getMessageId()
      )
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void removeMessage(AuthenticatedClientRequest client) {
    RemoveMessage payload = (RemoveMessage) client.getPayload();
    if (
      !GlobalServices.channels.isMessageSender(
        payload.getUserId(), 
        payload.getChannelId(),
        payload.getMessageId()
      )
    ) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Cannot remove message")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to remove msg:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getMessageId()
        )
      );
      return;
    }
    GlobalServices.channels.removeMessage(
      payload.getChannelId(), 
      payload.getMessageId()
    );

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Removed msg:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getMessageId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void changePassword(AuthenticatedClientRequest client) {
    ChangePassword payload = (ChangePassword) client.getPayload();
    boolean success = GlobalServices.users.changePassword(
      payload.getUserId(), 
      payload.getOriginalPassword(), 
      payload.getNewPassword()
    );
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, 
          payload.getId(), 
          "Incorrect original password"
        )
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to change password",
          this.getUsername(payload), 
          payload.getUserId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Changed password", 
        this.getUsername(payload),
        payload.getUserId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void sendMessage(AuthenticatedClientRequest client) {
    MessageToServer payload = (MessageToServer) client.getPayload();
    boolean success = GlobalServices.channels.addMessage(
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
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to send message to channel:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getChannelId()
        )
      );
      return;
    }
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Sent message to channel:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getChannelId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void sendFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestToServer payload = (FriendRequestToServer) client.getPayload();
    String recipientId = GlobalServices.users.getUserId(payload.getRecipientName());
    boolean success = GlobalServices.users.sendFriendRequest(
      payload.getUserId(), 
      recipientId,
      payload.getRequestMessage()
    );

    // sending request to a nonexistent user
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Recipient does not exist")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Failed to send friend req to user:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getRecipientName()
        )
      );
      return;
    }
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Sent friend req to user:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getRecipientName()
      )
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void respondFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestResponse payload = (FriendRequestResponse) client.getPayload();
    boolean success;
    if (payload.isAccepted()) {
      success = GlobalServices.users.acceptFriendRequest(
        payload.getUserId(), 
        payload.getRequesterId()
      );
    } else {
      success = GlobalServices.users.rejectFriendRequest(
        payload.getUserId(), 
        payload.getRequesterId()
      );
    }
    if (!success) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Error responding to friend request")
      );

      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error responding to friend req from user:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getRequesterId()
        )
      );
      return;
    }
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Responded to friend req from user:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getRequesterId()
      )
    );
    
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  // TODO: make sure the owner/invited user aren't blocked
  private void createChannel(AuthenticatedClientRequest client) {
    CreateChannel payload = (CreateChannel) client.getPayload();
    GlobalServices.channels.createGroupChannel(
      payload.getParticipants(), 
      payload.getName(), 
      payload.getUserId()
    );

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: created a new group channel", 
        this.getUsername(payload),
        payload.getUserId()
      )
    );

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
  }

  private void requestMessages(AuthenticatedClientRequest client) {
    RequestMessages payload = (RequestMessages) client.getPayload();
    Message[] msgs = GlobalServices.channels.getMessages(
      payload.getChannelId(), 
      payload.getCreated(),
      payload.getQuantity()
    );

    if (msgs == null) {
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, payload.getId(), "Error retrieving messages")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "%s:%s: Error retrieving messages for channel:%s", 
          this.getUsername(payload),
          payload.getUserId(),
          payload.getChannelId()
        )
      );
      return;
    }

    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format(
        "%s:%s: Retrieved messages for channel:%s", 
        this.getUsername(payload),
        payload.getUserId(),
        payload.getChannelId()
      )
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );

    PayloadSender.send(
      client.getClientOut(), 
      new MessagesToClient(1, payload.getChannelId(), msgs)
    );

  }


  private String getUsername(AuthenticatablePayload payload) {
    return GlobalServices.users.getUsername(payload.getUserId());
  }

}
