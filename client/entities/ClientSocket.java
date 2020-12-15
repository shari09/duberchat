package client.entities;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.PriorityBlockingQueue;

import client.resources.GlobalClient;
import client.services.ChannelServices;
import common.entities.Constants;
import common.entities.Message;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.BlockUser;
import common.entities.payload.client_to_server.CancelFriendRequest;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.CreateChannel;
import common.entities.payload.client_to_server.KeepAlive;
import common.entities.payload.client_to_server.RemoveFriend;
import common.entities.payload.client_to_server.UpdateStatus;
import common.entities.payload.server_to_client.Attachment;
import common.entities.payload.server_to_client.AttachmentToClient;
import common.entities.payload.server_to_client.ClientChannelsUpdate;
import common.entities.payload.server_to_client.ClientFriendsUpdate;
import common.entities.payload.server_to_client.ClientInfo;
import common.entities.payload.server_to_client.ClientRequestStatus;
import common.entities.payload.server_to_client.MessageUpdateToClient;
import common.entities.payload.server_to_client.MessagesToClient;
import common.entities.payload.server_to_client.ServerBroadcast;

/**
 * The client socket for handling socket connection and payloads.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientSocket implements Runnable {

  private static int heartbeatFrequency = Constants.HEARTBEAT_FREQUENCY;

  private Socket socket;
  private InputStream rawInput;
  private ObjectInputStream input;
  private ObjectOutputStream output;

  private PriorityBlockingQueue<Payload> payloadsToSend;
  private PriorityBlockingQueue<Payload> payloadsReceived;
  private ConcurrentHashMap<String, Payload> pendingRequests;
  private LinkedHashSet<ClientSocketListener> listeners;

  private boolean running;
  private long lastActiveTimeMills;
  private long lastHeartBeatTimeMills;
  private Point latestMousePoint;

  public ClientSocket(String host, int port) throws IOException {
    this.socket = new Socket(host, port);    
    this.rawInput = this.socket.getInputStream();
    this.input = new ObjectInputStream(this.rawInput);
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.payloadsToSend = new PriorityBlockingQueue<Payload>();
    this.payloadsReceived = new PriorityBlockingQueue<Payload>();
    this.pendingRequests = new ConcurrentHashMap<String, Payload>();
    this.listeners = new LinkedHashSet<ClientSocketListener>();
    this.running = true;
    this.lastActiveTimeMills = System.currentTimeMillis();
    this.latestMousePoint = MouseInfo.getPointerInfo().getLocation();
    this.sendHeartbeat();
  }

  public void run() {
    while (this.running) {
      this.updateMouseMovement();

      // send heartbeat
      if (this.lastActiveTimeMills - this.lastHeartBeatTimeMills >= ClientSocket.heartbeatFrequency) {
        this.sendHeartbeat();
      }

      synchronized (this.payloadsToSend) {
        if (this.payloadsToSend.size() > 0) {
          try {
              Payload payloadToSend = this.payloadsToSend.poll();
              if (payloadToSend.getType() != PayloadType.KEEP_ALIVE){
                System.out.println(payloadToSend.toString());
              }
              this.output.writeObject(payloadToSend);
              this.pendingRequests.put(payloadToSend.getId(), payloadToSend);

          } catch (SocketTimeoutException e) {
            this.running = false;
          } catch (SocketException e) {
            this.running = false;
              
          } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Failed to write payload");
          }
        }
      }
      
      try {
        if (this.rawInput.available() > 0) {
          Payload payload = (Payload)this.input.readObject();
          this.payloadsReceived.add(payload);
          System.out.println("Response received");
          System.out.println(payload.toString());
          this.processPayload();
        }
        
      } catch (SocketTimeoutException e) {
        this.running = false;
      } catch (SocketException e) {
        this.running = false;
            
      } catch (Exception e) {
        System.out.println("Failed to receive/process response from server");
        e.printStackTrace();
      }
    }

    try {
      this.socket.close();
      this.input.close();
      this.output.close();
    } catch (SocketException socketException) {
      this.notifyRequestStatus(PayloadType.KEEP_ALIVE, false, "You have been disconnected");
    } catch (Exception exception) {
      exception.printStackTrace();
      this.notifyRequestStatus(PayloadType.KEEP_ALIVE, false, "An error has occurred");
    }
    System.exit(0);
  }

  public synchronized void close() throws IOException {
    this.running = false;
  }

  public synchronized void sendPayload(Payload payloadToSend) {
    if (!this.running) {
      return;
    }
    this.payloadsToSend.add(payloadToSend);
  }

  public synchronized void addListener(ClientSocketListener listener) {
    this.listeners.add(listener);
  }

  public synchronized void removeListener(ClientSocketListener listener) {
    this.listeners.remove(listener);
  }

  public void updateLastActiveTime() {
    this.lastActiveTimeMills = System.currentTimeMillis();
  }

  private synchronized void processPayload() {
    // before the data is initialized, store the payloads in the queue
    Payload payload = this.payloadsReceived.peek();
    if (payload == null) {
      return;
    }
    if (
      !GlobalClient.hasData()
      && (payload.getType() != PayloadType.CLIENT_INFO)
      && (payload.getType() != PayloadType.CLIENT_REQUEST_STATUS)
    ) {
      System.out.println("ouch");
      return;
    }
    System.out.println("hi");
    switch (payload.getType()) {
      case CLIENT_REQUEST_STATUS:
        this.processRequestStatus((ClientRequestStatus)payload);
        break;

      case CLIENT_INFO:
        GlobalClient.clientData = ((ClientInfo)payload).getClientData();
        this.notifyClientDataUpdate();
        break;

      case CLIENT_FRIENDS_UPDATE:
        ClientFriendsUpdate friendsUpdate = (ClientFriendsUpdate)payload;
        System.out.println(friendsUpdate.getFriends());
        System.out.println(friendsUpdate.getIncomingFriendRequests());
        System.out.println(friendsUpdate.getOutgoingFriendRequests());
        GlobalClient.clientData.setFriends(friendsUpdate.getFriends());
        GlobalClient.clientData.setIncomingFriendRequests(friendsUpdate.getIncomingFriendRequests());
        GlobalClient.clientData.setOutgoingFriendRequests(friendsUpdate.getOutgoingFriendRequests());
        System.out.println("CLIENT_FRIENDS_UPDATE");
        this.notifyClientDataUpdate();
        break;

      case CLIENT_CHANNELS_UPDATE:
        ClientChannelsUpdate channelsUpdate = (ClientChannelsUpdate)payload;
        System.out.println(channelsUpdate);System.out.println(channelsUpdate.getChannels());
        GlobalClient.clientData.setChannels(channelsUpdate.getChannels());
        this.notifyClientDataUpdate();
        break;

      case MESSAGES_TO_CLIENT:
        MessagesToClient messagesUpdate = (MessagesToClient)payload;
        ChannelServices.addMessages(messagesUpdate.getChannelId(), messagesUpdate.getMessages());
        this.notifyClientDataUpdate();
        break;

      case ATTACHMENT_TO_CLIENT:
        AttachmentToClient attachmentPayload = (AttachmentToClient)payload;
        Attachment attachment = attachmentPayload.getAttachment();
        this.saveAttachment(attachment);
        break;

      case MESSAGE_UPDATE_TO_CLIENT:
        this.processMessageUpdate((MessageUpdateToClient)payload);
        this.notifyClientDataUpdate();
        break;

      case SERVER_BROADCAST:
        this.notifyServerBroadcast((ServerBroadcast)payload);
        break;

      default:
        System.out.println("unknown payload type");
        break;
    }
    this.payloadsReceived.poll();
    System.out.println("processed payload " + payload);
  }

  private synchronized void processMessageUpdate(MessageUpdateToClient messageUpdate) {
    String channelId = messageUpdate.getChannelId();
    Message updatedMessage = messageUpdate.getMessage();

    ConcurrentSkipListSet<Message> messages = GlobalClient.messagesData.get(channelId);
    if (messages == null) {
      GlobalClient.messagesData.put(channelId, new ConcurrentSkipListSet<Message>());
      messages = GlobalClient.messagesData.get(channelId);
    }
   
    switch (messageUpdate.getUpdateType()) {
      case NEW:
        messages.add(updatedMessage);
        break;

      case EDIT:
        Iterator<Message> editIterator = messages.iterator();
        while (editIterator.hasNext()) {
          Message msg = editIterator.next();
          if (msg.getId().equals(updatedMessage.getId())) {
            editIterator.remove();
            break;
          }
        }
        messages.add(updatedMessage);
        break;

      case REMOVE:
        Iterator<Message> removeIterator = messages.iterator();
        while (removeIterator.hasNext()) {
          Message msg = removeIterator.next();
          if (msg.getId().equals(updatedMessage.getId())) {
            removeIterator.remove();
            break;
          }
        }
        break;
    }
  }

  private synchronized void processRequestStatus(ClientRequestStatus requestStatus) {
    String originalPayloadId = requestStatus.getRequestPayloadId();
    Payload originalPayload = this.pendingRequests.get(originalPayloadId);
    String errorMessage = requestStatus.getErrorMessage();

    if (originalPayload == null) {
      System.out.println("original client request not found");

    } else if (errorMessage != null) {
      this.notifyRequestStatus(originalPayload.getType(), false, errorMessage);
      System.out.println("An error has occurred! (" + errorMessage + ")");

    } else {
      // error message is null: request success
      switch (originalPayload.getType()) {
        case ADD_PARTICIPANT:
          this.notifyRequestStatus(
            PayloadType.ADD_PARTICIPANT,
            true,
            "Successfully added participant to channel"
          );
          break;

        case BLACKLIST_USER:
          this.notifyRequestStatus(
            PayloadType.BLACKLIST_USER,
            true,
            "Successfully blacklisted user"
          );
          break;
        
        case BLOCK_USER:
          this.notifyRequestStatus(
            PayloadType.BLOCK_USER,
            true,
            "Successfully blocked " + ((BlockUser)originalPayload).getBlockUsername()
          );
          break;
        
        case CANCEL_FRIEND_REQUEST:
          this.notifyRequestStatus(
            PayloadType.CANCEL_FRIEND_REQUEST,
            true,
            "Successfully cancelled friend request"
          );
          break;

        case CHANGE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.CHANGE_CHANNEL,
            true,
            "Successfully changed channel field"
          );
          break;

        case CHANGE_PASSWORD:
          this.notifyRequestStatus(
            PayloadType.CHANGE_PASSWORD,
            true,
            "Successfully changed password"
          );
          break;
        
        case CHANGE_PROFILE:
          this.changeProfile((ChangeProfile)originalPayload);
          break;

        case CREATE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.CREATE_CHANNEL,
            true,
            "Successfully created channel: " + ((CreateChannel)originalPayload).getName()
          );
          break;
        
        case EDIT_MESSAGE:
          this.notifyRequestStatus(
            PayloadType.EDIT_MESSAGE,
            true,
            "Successfully edited message"
          );
          break;

          
        case FRIEND_REQUEST:
          this.notifyRequestStatus(
            PayloadType.FRIEND_REQUEST,
            true,
            "Successfully sent friend request"
          );
          break;

        case FRIEND_REQUEST_RESPONSE:
          this.notifyRequestStatus(
            PayloadType.FRIEND_REQUEST_RESPONSE,
            true,
            "Successfully sent friend request response"
          );
          break;

        case KEEP_ALIVE:
          this.notifyRequestStatus(
            PayloadType.KEEP_ALIVE,
            true,
            "Successfully refreshed inactivity timing"
          );

        case LEAVE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.LEAVE_CHANNEL,
            true,
            "Successfully left channel"
          );
          break;

        case LOGIN:
          this.notifyRequestStatus(
            PayloadType.LOGIN,
            true,
            "Successfully logged in"
          );
          break;

        case MESSAGE_TO_SERVER:
          this.notifyRequestStatus(
            PayloadType.MESSAGE_TO_SERVER,
            true,
            "Successfully sent message"
          );
          break;
        
        case NEW_USER:
          this.notifyRequestStatus(
            PayloadType.NEW_USER,
            true,
            "Successfully created account and logged in"
          );
          break;

        case REMOVE_FRIEND:
          this.notifyRequestStatus(
            PayloadType.REMOVE_FRIEND,
            true,
            "Successfully removed friend"
          );
          break;

        case REMOVE_MESSAGE:
          this.notifyRequestStatus(
            PayloadType.REMOVE_MESSAGE,
            true,
            "Successfully removed message"
          );
          break;
        
        case REMOVE_PARTICIPANT:
          this.notifyRequestStatus(
            PayloadType.REMOVE_PARTICIPANT,
            true,
            "Successfully removed participant from channel"
          );
          break;
        
        case REQUEST_ATTACHMENT:
          this.notifyRequestStatus(
            PayloadType.REQUEST_ATTACHMENT,
            true,
            "Successfully received attachment"
          );
          break;
        
        case REQUEST_MESSAGES:
          this.notifyRequestStatus(
            PayloadType.REQUEST_MESSAGES,
            true,
            "Successfully received requested messages"
          );
          break;

        case TRANSFER_OWNERSHIP:
          this.notifyRequestStatus(
            PayloadType.TRANSFER_OWNERSHIP,
            true,
            "Successfully transferred ownership"
          );

        case UPDATE_STATUS:
          this.updateUserStatus((UpdateStatus)originalPayload);
          break;

        default:
          System.out.println("unknown payload type");
          break;
        
      }
    }
    // request resolved
    this.pendingRequests.remove(originalPayloadId);
  }

  private synchronized void saveAttachment(Attachment attachment) {
    File directory = new File(GlobalClient.getDownloadFolderPath());
    if (!directory.exists()) {
      directory.mkdirs();
    }
    Path filePath = directory.toPath().resolve(attachment.getName());

    int versionNumber = 0;
    boolean fileExists = Files.isRegularFile(filePath);
    while (fileExists) {
      versionNumber++;
      filePath = directory.toPath().resolve(
        " (" + Integer.toString(versionNumber) + ") " + attachment.getName()
      );
      fileExists = Files.isRegularFile(filePath);
    }
    try {
      FileOutputStream out = new FileOutputStream(filePath.toString());
      out.write(attachment.getData());
      out.close();
      this.notifyRequestStatus(
        PayloadType.REQUEST_ATTACHMENT,
        true,
        "Successfully downloaded " + attachment.getName() + " to " + filePath
      );
    } catch (IOException e) {
      e.printStackTrace();
      this.notifyRequestStatus(
        PayloadType.REQUEST_ATTACHMENT,
        false,
        "Failed to downloaded " + attachment.getName()
      );
    }
    
  }

  private synchronized void changeProfile(ChangeProfile changeProfile) {
    String newValue = changeProfile.getNewValue();
    switch (changeProfile.getFieldToChange()) {
      case USERNAME:
        GlobalClient.clientData.setUsername(newValue);
        this.notifyClientDataUpdate();
        this.notifyRequestStatus(
          PayloadType.CHANGE_PROFILE,
          true,
          "Successfully updated username"
        );
        break;

      case DESCRIPTION:
        GlobalClient.clientData.setDescription(newValue);
        this.notifyClientDataUpdate();
        this.notifyRequestStatus(
          PayloadType.CHANGE_PROFILE,
          true,
          "Successfully updated description"
        );
        break;

      default:
        this.notifyRequestStatus(
          PayloadType.CHANGE_PROFILE, 
          false,
          "Unknown profile field"
        );
        System.out.println("unknown profile field");
    }
  }

  private synchronized void updateUserStatus(UpdateStatus updateStatus) {
    GlobalClient.clientData.setStatus(updateStatus.getStatus());
    this.notifyClientDataUpdate();
    this.notifyRequestStatus(
      PayloadType.UPDATE_STATUS,
      true,
      "Successfully updated status"
    );
  }

  private void notifyClientDataUpdate() {
    synchronized (GlobalClient.clientData) {
      for (ClientSocketListener listener: this.listeners) {
        listener.clientDataUpdated(GlobalClient.clientData);
      }
    }
  }

  private synchronized void notifyServerBroadcast(ServerBroadcast broadcast) {
    for (ClientSocketListener listener: this.listeners) {
      listener.serverBroadcastReceived(broadcast);
    }
  }

  private synchronized void sendHeartbeat() {
    this.sendPayload(new KeepAlive());
    this.lastHeartBeatTimeMills = System.currentTimeMillis();
  }

  private synchronized void notifyRequestStatus(
    PayloadType payloadType,
    boolean successful,
    String notifMessage
  ) {
    for (ClientSocketListener listener: this.listeners) {
      listener.clientRequestStatusReceived(payloadType, successful, notifMessage);
    }
  }
  
  private void updateMouseMovement() {
    Point curPoint = MouseInfo.getPointerInfo().getLocation();
    if (
      (curPoint.getX() != this.latestMousePoint.getX())
      || (curPoint.getY() != this.latestMousePoint.getY())
    ) {
      this.updateLastActiveTime();
      this.latestMousePoint = curPoint;
    }
  }
}
