package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Message;
import common.entities.payload.client_to_server.AddParticipant;
import common.entities.payload.client_to_server.BlacklistUser;
import common.entities.payload.client_to_server.BlockUser;
import common.entities.payload.client_to_server.CancelFriendRequest;
import common.entities.payload.client_to_server.ChangeChannel;
import common.entities.payload.client_to_server.ChangePassword;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.CreateChannel;
import common.entities.payload.client_to_server.EditMessage;
import common.entities.payload.client_to_server.FriendRequestResponse;
import common.entities.payload.client_to_server.FriendRequestToServer;
import common.entities.payload.client_to_server.LeaveChannel;
import common.entities.payload.client_to_server.MessageToServer;
import common.entities.payload.client_to_server.RemoveFriend;
import common.entities.payload.client_to_server.RemoveMessage;
import common.entities.payload.client_to_server.RemoveParticipant;
import common.entities.payload.client_to_server.RequestAttachment;
import common.entities.payload.client_to_server.RequestMessages;
import common.entities.payload.client_to_server.TransferOwnership;
import common.entities.payload.client_to_server.UpdateStatus;
import common.entities.payload.server_to_client.Attachment;
import common.entities.payload.server_to_client.AttachmentToClient;
import common.entities.payload.server_to_client.MessagesToClient;
import server.entities.AuthenticatedClientRequest;
import server.entities.EventType;
import server.entities.LogType;

/**
 * Deals with authenticated payloads.
 * See {@link AuthenticatedPayloadProcessor#onEvent(Object, EventType)}
 * for all the payload this processor accepts.
 * <p>
 * Depending on what {@link PayloadType} this is, it will
 * either use appropriate services from {@link GlobalServices}
 * and handle the requests properly.
 * <p>
 * [It will take too long to Javadoc each method and is pretty pointless]
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see PayloadProcessor
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
          CommunicationService.log(String.format(
            "Incorrect payload: %s", 
            client.getPayload().getType()
          ), LogType.ERROR);
          break;
      }

    }
  }

  private void transferOwnership(AuthenticatedClientRequest client) {
    TransferOwnership payload = (TransferOwnership) client.getPayload();
    boolean success = GlobalServices.channels.transferOwnership(
      payload.getUserId(),
      payload.getRecipientId(), 
      payload.getChannelId()
    );

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
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
        CommunicationService.sendAuthenticatedResponse(
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
        CommunicationService.sendAuthenticatedResponse(
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
    CommunicationService.sendAuthenticatedResponse(
      client, 
      true, 
      String.format("Updating status to %s", payload.getStatus()), 
      "Failed to update status"
    );
  }

  private void requestAttachment(AuthenticatedClientRequest client) {
    RequestAttachment payload = (RequestAttachment) client.getPayload();
    Attachment attachment = GlobalServices.channels.getAttachment(payload.getAttachmentId());
    
    boolean success = false;
    if (attachment != null) {
      success = true;
    } 

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Downloading attachment:%s",
        payload.getAttachmentId()
      ), 
      "Unable to download attachment"
    );

    if (success) {
      CommunicationService.send(
        client.getClientOut(), 
        new AttachmentToClient(1, attachment)
      );
    }
    
  }

  private void removeParticipant(AuthenticatedClientRequest client) {
    RemoveParticipant payload = (RemoveParticipant) client.getPayload();
    boolean success = GlobalServices.channels.removeParticipant(
      payload.getUserId(),
      payload.getParticipantId(),
      payload.getChannelId()
    );

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Removing participant:%s from channel:%s",
        payload.getParticipantId(),
        payload.getChannelId()
      ), 
      "Cannot remove participant"
    );

  }

  private void blacklistUser(AuthenticatedClientRequest client) {
    BlacklistUser payload = (BlacklistUser) client.getPayload();
    boolean success = GlobalServices.channels.blacklistUser(
      payload.getUserId(),
      payload.getParticipantId(), 
      payload.getChannelId()
    );

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Blacklisting participant:%s from channel:%s",
        payload.getParticipantId(),
        payload.getChannelId()
      ), 
      "Cannot blacklist participant"
    );
  }

  private void addParticipant(AuthenticatedClientRequest client) {
    AddParticipant payload = (AddParticipant) client.getPayload();
    String errorMsg = "";
    boolean success = true;
    if (!GlobalServices.users.isFriend( //not friend
        payload.getUserId(), 
        payload.getParticipantId()
    )) {
      errorMsg = "User is not your friend";
      success = false;
    } else if (!GlobalServices.channels.addParticipant( //blacklisted
        payload.getParticipantId(), 
        payload.getChannelId()
    )) {
      errorMsg = "User blacklisted";
      success = false;
    }

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Error adding user:%s to channel:%s",
        payload.getParticipantId(),
        payload.getChannelId()
      ), 
      errorMsg
    );

  }

  private void blockUser(AuthenticatedClientRequest client) {
    BlockUser payload = (BlockUser) client.getPayload();

    boolean success = GlobalServices.users.blockUser(
      payload.getUserId(), 
      payload.getBlockUsername()
    );

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Editing message:%s",
        payload.getMessageId()
      ), 
      "Cannot edit message"
    );

    if (success) {
      GlobalServices.channels.editMessage(
        payload.getChannelId(), 
        payload.getMessageId(), 
        payload.getNewContent()
      );
    }
  }

  private void removeMessage(AuthenticatedClientRequest client) {
    RemoveMessage payload = (RemoveMessage) client.getPayload();
    boolean success = GlobalServices.channels.isMessageSender(
      payload.getUserId(), 
      payload.getChannelId(),
      payload.getMessageId()
    );

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Removing message:%s", 
        payload.getMessageId()
      ), 
      "Cannot remove message"
    );

    if (success) {
      GlobalServices.channels.removeMessage(
        payload.getChannelId(), 
        payload.getMessageId()
      );
    }
    
  }

  private void changePassword(AuthenticatedClientRequest client) {
    ChangePassword payload = (ChangePassword) client.getPayload();
    boolean success = GlobalServices.users.changePassword(
      payload.getUserId(), 
      payload.getOriginalPassword(), 
      payload.getNewPassword()
    );

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      "Changing password",
      "Incorrect original password"
    );
    
  }

  private void sendMessage(AuthenticatedClientRequest client) {
    MessageToServer payload = (MessageToServer) client.getPayload();

    boolean success = GlobalServices.channels.allowMessaging(
      payload.getUserId(),
      payload.getChannelId()
    );
    String msg = "";
    if (success) {
      success = GlobalServices.channels.addMessage(
        payload.getUserId(), 
        payload.getChannelId(),
        payload.getContent(), 
        payload.getAttachment(), 
        payload.getAttachmentName()
      );
      if (!success) {
        msg = "Channel not found";
      }
    } else {
      msg = "You have been blocked/blacklisted";
    }
    
    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Sending message to channel:%s", 
        payload.getChannelId()
      ),
      msg
    );
  
  }

  private void sendFriendRequest(AuthenticatedClientRequest client) {
    FriendRequestToServer payload = (FriendRequestToServer) client.getPayload();
    String recipientId = GlobalServices.users.getUserId(payload.getRecipientName());
    boolean success = false;
    String msg = "";
    if (recipientId == null) {
      msg = "Recipient does not exist";
    } else if (GlobalServices.users.isBlocked(recipientId, payload.getUserId())) {
      msg = "You are blocked";
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
    
    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
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

    CommunicationService.sendAuthenticatedResponse(
      client, 
      success, 
      String.format(
        "Retrieving messages for channel:%s",
        payload.getChannelId()
      ),
      "Error retrieving messages"
    );

    if (success) {
      CommunicationService.send(
        client.getClientOut(), 
        new MessagesToClient(1, payload.getChannelId(), msgs)
      );
    }

  }

  private void removeFriend(AuthenticatedClientRequest client) {
    RemoveFriend payload = (RemoveFriend)client.getPayload();
    GlobalServices.users.removeFriend(payload.getUserId(), payload.getFriendId());
    CommunicationService.sendAuthenticatedResponse(
      client, 
      true, 
      String.format("Removing friend:%s", payload.getFriendId()),
      "Error removing friend"
    );

  }

}