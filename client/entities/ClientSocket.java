package client.entities;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.net.SocketTimeoutException;

import client.resources.GlobalClient;
import common.entities.Constants;
import common.entities.Attachment;
import common.entities.Message;
import common.entities.PrivateChannelMetadata;
import common.entities.UserStatus;
import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.payload.*;

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

  private PriorityBlockingQueue<Payload> payloadQueue;
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
    this.payloadQueue = new PriorityBlockingQueue<Payload>();
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

      synchronized (this.payloadQueue) {
        if (this.payloadQueue.size() > 0) {
          try {
              Payload payloadToSend = this.payloadQueue.poll();
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
          System.out.println("Response received");
          System.out.println(payload.toString());
          this.processPayload(payload);
        }
        
      } catch (SocketTimeoutException e) {
        this.running = false;
      } catch (SocketException e) {
        this.running = false;
            
      } catch (Exception e) {
        System.out.println("Failed to receive response from server");
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
    this.payloadQueue.add(payloadToSend);
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

  private synchronized void processPayload(Payload payload) {
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
        GlobalClient.clientData.setFriends(friendsUpdate.getFriends());
        GlobalClient.clientData.setIncomingFriendRequests(friendsUpdate.getIncomingFriendRequests());
        GlobalClient.clientData.setOutgoingFriendRequests(friendsUpdate.getOutgoingFriendRequests());
        this.notifyClientDataUpdate();
        break;

      case CLIENT_CHANNELS_UPDATE:
        ClientChannelsUpdate channelsUpdate = (ClientChannelsUpdate)payload;
        GlobalClient.clientData.setChannels(channelsUpdate.getChannels());
        this.notifyClientDataUpdate();
        break;

      case MESSAGES_TO_CLIENT:
        MessagesToClient messagesUpdate = (MessagesToClient)payload;
        this.addMessages(messagesUpdate.getChannelId(), messagesUpdate.getMessages());
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

      default:
        System.out.println("unknown payload type");
        break;
    }
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
        case NEW_USER:
          this.notifyRequestStatus(
            PayloadType.NEW_USER,
            true,
            "Successfully created account and logged in"
          );
          break;

        case LOGIN:
          this.notifyRequestStatus(
            PayloadType.LOGIN,
            true,
            "Successfully logged in"
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

        case UPDATE_STATUS:
          this.updateUserStatus((UpdateStatus)originalPayload);
          break;

        case CHANGE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.CHANGE_CHANNEL,
            true,
            "Successfully changed channel field"
          );
          break;

        case MESSAGE_TO_SERVER:
          this.notifyRequestStatus(
            PayloadType.MESSAGE_TO_SERVER,
            true,
            "Successfully sent message"
          );
          break;

        // case REMOVE_MESSAGE:
        //   this.notifyRequestStatus(
        //     PayloadType.REMOVE_MESSAGE,
        //     true,
        //     "Successfully removed message"
        //   );
        //   break;
        
        // case EDIT_MESSAGE:
        //   this.notifyRequestStatus(
        //     PayloadType.EDIT_MESSAGE,
        //     true,
        //     "Successfully edited message"
        //   );
        //   break;

        case REQUEST_MESSAGES:
          this.notifyRequestStatus(
            PayloadType.REQUEST_MESSAGES,
            true,
            "Successfully received requested messages"
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

        case REQUEST_ATTACHMENT:
          this.notifyRequestStatus(
            PayloadType.REQUEST_ATTACHMENT,
            true,
            "Successfully received attachment"
          );
          break;

        case CREATE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.CREATE_CHANNEL,
            true,
            "Successfully created channel: " + ((CreateChannel)originalPayload).getName()
          );
          break;

        case BLOCK_USER:
          this.notifyRequestStatus(
            PayloadType.BLOCK_USER,
            true,
            "Successfully blocked " + ((BlockUser)originalPayload).getBlockUsername()
          );
          break;

        case ADD_PARTICIPANT:
          this.notifyRequestStatus(
            PayloadType.ADD_PARTICIPANT,
            true,
            "Successfully added participant to channel"
          );
          break;

        case REMOVE_PARTICIPANT:
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

        case LEAVE_CHANNEL:
          this.notifyRequestStatus(
            PayloadType.LEAVE_CHANNEL,
            true,
            "Successfully left channel"
          );
          break;

        case TRANSFER_OWNERSHIP:
          this.notifyRequestStatus(
            PayloadType.TRANSFER_OWNERSHIP,
            true,
            "Successfully transferred ownership"
          );

        case KEEP_ALIVE:
          this.notifyRequestStatus(
            PayloadType.KEEP_ALIVE,
            true,
            "Successfully refreshed inactivity timing"
          );

        default:
          System.out.println("unknown payload type");
          break;
        
      }
    }
    
    this.pendingRequests.remove(originalPayloadId);
  }

  private synchronized void saveAttachment(Attachment attachment) {
    String filePath = GlobalClient.getDownloadFolderPath() + attachment.getName();
    int versionNumber = 0;
    boolean fileExists = Files.isRegularFile(Paths.get(filePath));
    while (fileExists) {
      versionNumber++;
      filePath = " (" + Integer.toString(versionNumber) + ") " + filePath;
      fileExists = Files.isRegularFile(Paths.get(filePath));
    }
    try {
      FileOutputStream out = new FileOutputStream(filePath);
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

  private void addMessages(String channelId, Message[] messages) {
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
    if (channelMessages != null) {
      for (Message msg: messages) {
        channelMessages.add(msg);
      }
    }
  }

  private void removeMessages(String channelId, Message[] messages) {
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
    if (channelMessages != null) {
      for (Message msg: messages) {
        channelMessages.remove(msg);
      }
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

  private synchronized void notifyClientDataUpdate() {
    for (ClientSocketListener listener: this.listeners) {
      listener.clientDataUpdated(GlobalClient.clientData);
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
