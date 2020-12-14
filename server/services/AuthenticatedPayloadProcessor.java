package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Attachment;
import common.entities.Message;
import common.entities.payload.AddParticipant;
import common.entities.payload.AttachmentToClient;
import common.entities.payload.AuthenticatablePayload;
import common.entities.payload.BlacklistUser;
import common.entities.payload.BlockUser;
import common.entities.payload.CancelFriendRequest;
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
import common.entities.payload.RemoveFriend;
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

  public AuthenticatedPayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
  }

  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_PAYLOAD, this);
  }

  @Override
  public void onEvent(Object newPayload, EventType eventType) {
    this.payloadQueue.add((AuthenticatedClientRequest) newPayload);
    //log
    AuthenticatablePayload payload = ((AuthenticatedClientRequest)newPayload).getPayload();
    this.log(String.format(
      "%s:%s: Sent authenticated payload:%s", 
      this.getUsername(payload),
      payload.getUserId(),
      payload.getType()
    ));
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
        case CANCEL_FRIEND_REQUEST:
          this.cancelFriendRequest(client);
          break;
        case FRIEND_REQUEST_RESPONSE:
          this.respondFriendRequest(client);
          break;
        case REMOVE_FRIEND:
          this.removeFriend(client);
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
          this.log(String.format(
            "Incorrect payload: %s", 
            client.getPayload().getType()
          ));
          break;
      }

    }
  }

  private void transferOwnership(AuthenticatedClientRequest client) {
    TransferOwnership payload = (TransferOwnership) client.getPayload();
    boolean success = GlobalServices.channels.transferOwnership(
      payload.getRecipientId(), 
      payload.getChannelId()
    );

    this.sendResponse(
      client, 
      success, 
      String.format("Transferring ownership to user:%s", payload.getRecipientId()),
      "Transfer failed"
    );

  }

  private void leaveChannel(AuthenticatedClientRequest client) {
    LeaveChannel payload = (LeaveChannel) client.getPayload();
    boolean success = GlobalServices.channels.leaveChannel(
      payload.getUserId(), 
      payload.getChannelId()
    );

    this.sendResponse(
      client, 
      success, 
      "Leaving channel", 
      "Error leaving channel"
    );

  }

  private void changeChannel(AuthenticatedClientRequest client) {
    ChangeChannel payload = (ChangeChannel) client.getPayload();
    boolean success = GlobalServices.channels.changeChannelSettings(
      payload.getChannelId(),
      payload.getFieldToChange(), 
      payload.getNewValue()
    );

    this.sendResponse(
      client, 
      success, 
      String.format("Changing channel %s", payload.getFieldToChange()), 
      "Error changing channel settings"
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
        this.sendResponse(
          client, 
          success, 
          String.format("Changing %s", payload.getFieldToChange()), 
          "Failed to change description"
        );
        break;
      case USERNAME:
        success = GlobalServices.users.changeUsername(
          payload.getUserId(), 
          payload.getNewValue()
        );
        this.sendResponse(
          client, 
          success, 
          String.format("Changing %s", payload.getFieldToChange()), 
          "Username taken"
        );
        break;
    }

  }

  private void updateStatus(AuthenticatedClientRequest client) {
    UpdateStatus payload = (UpdateStatus) client.getPayload();
    GlobalServices.users.updateUserStatus(
      payload.getUserId(), 
      payload.getStatus()
    );
    this.sendResponse(
      client, 
      true, 
      String.format("Updating status to %s", payload.getStatus()), 
      "Failed to update status"
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

    this.sendResponse(
      client, 
      success, 
      String.format("Blocking %s", payload.getBlockUsername()), 
      "Invalid username"
    );

  }

  private void editMessage(AuthenticatedClientRequest client) {
    EditMessage payload = (EditMessage) client.getPayload();
    boolean success = GlobalServices.channels.isMessageSender(
      payload.getUserId(), 
      payload.getChannelId(),
      payload.getMessageId()
    );

    if (success) {
      GlobalServices.channels.editMessage(
        payload.getChannelId(), 
        payload.getMessageId(), 
        payload.getNewContent()
      );
    }

    this.sendResponse(
      client, 
      success, 
      String.format(
        "Editing message:%s",
        payload.getMessageId()
      ), 
      "Cannot edit message"
    );
    
  }

  private void removeMessage(AuthenticatedClientRequest client) {
    RemoveMessage payload = (RemoveMessage) client.getPayload();
    boolean success = GlobalServices.channels.isMessageSender(
      payload.getUserId(), 
      payload.getChannelId(),
      payload.getMessageId()
    );

    if (success) {
      GlobalServices.channels.removeMessage(
        payload.getChannelId(), 
        payload.getMessageId()
      );
    }

    this.sendResponse(
      client, 
      success, 
      String.format(
        "Removing message:%s", 
        payload.getMessageId()
      ), 
      "Cannot remove message"
    );
    
  }

  private void changePassword(AuthenticatedClientRequest client) {
    ChangePassword payload = (ChangePassword) client.getPayload();
    boolean success = GlobalServices.users.changePassword(
      payload.getUserId(), 
      payload.getOriginalPassword(), 
      payload.getNewPassword()
    );

    this.sendResponse(
      client, 
      success, 
      "Changing password",
      "Incorrect original password"
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
    this.sendResponse(
      client, 
      success, 
      String.format(
        "Sending message to channel:%s", 
        payload.getChannelId()
      ),
      "Message sending failure"
    );
  
  }

  private void sendFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestToServer payload = (FriendRequestToServer) client.getPayload();
    String recipientId = GlobalServices.users.getUserId(payload.getRecipientName());
    boolean success = false;
    String msg = "";
    if (recipientId == null) {
      msg = "Recipient does not exist";
    } else {
      success = GlobalServices.users.sendFriendRequest(
        payload.getUserId(), 
        recipientId,
        payload.getRequestMessage()
      );
      if (!success) {
        msg = "Duplicate friend request";
      }
    }
    
    this.sendResponse(
      client, 
      success, 
      String.format(
        "Sending friend request to user:%s",
        payload.getRecipientName()
      ),
      msg
    );
  }

  private void cancelFriendRequest(AuthenticatedClientRequest client) {
    CancelFriendRequest payload = (CancelFriendRequest)client.getPayload();
    boolean success = GlobalServices.users.cancelFriendRequest(
      payload.getUserId(), 
      payload.getRecipientId()
    );

    this.sendResponse(
      client, 
      success, 
      String.format(
        "Cancelling friend request to user:%s",
        payload.getRecipientId()
      ), 
      "Error cancelling friend request"
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

    this.sendResponse(
      client, 
      success, 
      String.format(
        "Responding to friend request from user:%s",
        payload.getRequesterId()
      ),
      "Error responding to friend request"
    );
  }

  private void createChannel(AuthenticatedClientRequest client) {
    CreateChannel payload = (CreateChannel) client.getPayload();
    GlobalServices.channels.createGroupChannel(
      payload.getParticipants(), 
      payload.getName(), 
      payload.getUserId()
    );

    this.sendResponse(
      client, 
      true, 
      "Creating a new group channel",
      "Error creating a new group channel"
    );
  }

  private void requestMessages(AuthenticatedClientRequest client) {
    RequestMessages payload = (RequestMessages) client.getPayload();
    Message[] msgs = GlobalServices.channels.getMessages(
      payload.getChannelId(), 
      payload.getCreated(),
      payload.getQuantity()
    );

    boolean success;
    if (msgs == null) {
      success = false;
    } else {
      success = true;
    }

    this.sendResponse(
      client, 
      success, 
      String.format(
        "Retrieving messages for channel:%s",
        payload.getChannelId()
      ),
      "Error retrieving messages"
    );

    if (success) {
      PayloadSender.send(
        client.getClientOut(), 
        new MessagesToClient(1, payload.getChannelId(), msgs)
      );
    }

  }

  private void removeFriend(AuthenticatedClientRequest client) {
    RemoveFriend payload = (RemoveFriend)client.getPayload();
    GlobalServices.users.removeFriend(payload.getUserId(), payload.getFriendId());
    this.sendResponse(
      client, 
      true, 
      String.format("Removing friend:%s", payload.getFriendId()),
      "Error removing friend"
    );

  }


  private String getUsername(AuthenticatablePayload payload) {
    return GlobalServices.users.getUsername(payload.getUserId());
  }


  /**
   * 
   * @param client
   * @param success
   * @param logSuccess
   * @param logError
   * @param errorMsgToClient
   */
  private void sendResponse(
    AuthenticatedClientRequest client,
    boolean success,
    String log,
    String errorMsgToClient
  ) {
    AuthenticatablePayload payload = (AuthenticatablePayload)client.getPayload();
    if (!success) {
      this.sendError(client, errorMsgToClient);
      this.log(String.format(
        "|ERROR| %s:%s: %s", 
        this.getUsername(payload),
        payload.getUserId(),
        log
      ));
      return;
    }
    this.log(String.format(
      "|SUCCESS| %s:%s: %s", 
      this.getUsername(payload),
      payload.getUserId(),
      log
    ));
    this.sendSuccess(client);
  }

  private void sendSuccess(AuthenticatedClientRequest client) {
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, client.getPayload().getId(), null)
    );
  }

  private void sendError(AuthenticatedClientRequest client, String errorMsg) {
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, client.getPayload().getId(), errorMsg)
    );
  }

  private void log(String msg) {
    GlobalServices.serverEventQueue.emitEvent(EventType.NEW_LOG, 1, msg);
  }

}