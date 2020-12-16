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
import client.services.ClientSocketServices;
import common.entities.Constants;
import common.entities.Message;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.KeepAlive;
import common.entities.payload.client_to_server.UpdateStatus;
import common.entities.Attachment;
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
      // response from client_to_server payloads
      case CLIENT_REQUEST_STATUS:
        this.processRequestStatus((ClientRequestStatus)payload);
        break;

      // server to client payloads
      case ATTACHMENT_TO_CLIENT:
        AttachmentToClient attachmentPayload = (AttachmentToClient)payload;
        Attachment attachment = attachmentPayload.getAttachment();
        this.saveAttachment(attachment);
        break;

      case CLIENT_CHANNELS_UPDATE:
        System.out.println("update");
        ClientChannelsUpdate channelsUpdate = (ClientChannelsUpdate)payload;
        System.out.println("update1");
        GlobalClient.clientData.setChannels(channelsUpdate.getChannels());
        System.out.println("update2");
        this.notifyClientDataUpdate();
        System.out.println("update3");
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

      case CLIENT_INFO:
        GlobalClient.clientData = ((ClientInfo)payload).getClientData();
        this.notifyClientDataUpdate();
        break;

      case MESSAGES_TO_CLIENT:
        MessagesToClient messagesUpdate = (MessagesToClient)payload;
        ChannelServices.addMessages(messagesUpdate.getChannelId(), messagesUpdate.getMessages());
        this.notifyClientDataUpdate();
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
      // notify listeners
      this.notifyRequestStatus(originalPayload.getType(), false, errorMessage);
      System.out.println("An error has occurred! (" + errorMessage + ")");

    } else {
      // error message is null: request successful
      // update data locally for certain types of payloads
      switch (originalPayload.getType()) {
        case CHANGE_PROFILE:
          this.changeProfile((ChangeProfile)originalPayload);
          break;

        case UPDATE_STATUS:
          this.updateUserStatus((UpdateStatus)originalPayload);
          break;

        default:
          break;
      }
      // notify listeners
      this.notifyRequestStatus(
        originalPayload.getType(),
        true,
        ClientSocketServices.getRequestSuccessNotifMessage(originalPayload)
      );
    }
    
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
        break;

      case DESCRIPTION:
        GlobalClient.clientData.setDescription(newValue);
        this.notifyClientDataUpdate();
        break;

      default:
        break;
    }
  }

  private synchronized void updateUserStatus(UpdateStatus updateStatus) {
    GlobalClient.clientData.setStatus(updateStatus.getStatus());
    this.notifyClientDataUpdate();
  }

  private void notifyClientDataUpdate() {
//    synchronized (GlobalClient.clientData) {
      for (ClientSocketListener listener: this.listeners) {
        listener.clientDataUpdated(GlobalClient.clientData);
      }
//    }
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
