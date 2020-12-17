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

import client.resources.GlobalClient;
import client.resources.GlobalPayloadQueue;
import client.services.ChannelServices;
import client.services.ClientSocketServices;
import common.entities.Attachment;
import common.entities.Constants;
import common.entities.Message;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.KeepAlive;
import common.entities.payload.client_to_server.UpdateStatus;
import common.entities.payload.server_to_client.AttachmentToClient;
import common.entities.payload.server_to_client.ClientChannelsUpdate;
import common.entities.payload.server_to_client.ClientFriendsUpdate;
import common.entities.payload.server_to_client.ClientInfo;
import common.entities.payload.server_to_client.ClientRequestStatus;
import common.entities.payload.server_to_client.MessageUpdateToClient;
import common.entities.payload.server_to_client.MessagesToClient;
import common.entities.payload.server_to_client.ServerBroadcast;

/**
 * The main client socket for handling socket connection and payloads.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientSocket implements Runnable {
  /** The client socket used for connection. */
  private Socket socket;
  /** The raw InputStream for the socket. */
  private InputStream rawInput;
  /** The ObjectInputStream used to deserialize objects sent from the server. */
  private ObjectInputStream input;
  /** The ObjectOutputStream used to serialize objects sent to the server. */
  private ObjectOutputStream output;
  /**
   * Stores the client requests that have not received a request status update
   * from the server, with their payload ids as key.
   */
  private ConcurrentHashMap<String, Payload> pendingRequests;
  /** A set of listeners that listens to this ClientSocket's updates. */
  private LinkedHashSet<ClientSocketListener> listeners;
  /**
   * Whether or not the ClientSocket is running.
   */
  private boolean running;
  /**
   * The last time this ClientSocket has sent a KEEP_ALIVE payload to the server,
   * in milliseconds.
   */
  private long lastHeartBeatTimeMills;
  /** The most recent position of the user's mouse. */
  private Point latestMousePoint;

  /**
   * Constructs a new {@code ClientSocket} with a host and a port address.
   * @param host The name of the host.
   * @param port The port number where the server starts at.
   * @throws IOException
   */
  public ClientSocket(String host, int port) throws IOException {
    this.socket = new Socket(host, port);    
    this.rawInput = this.socket.getInputStream();
    this.input = new ObjectInputStream(this.rawInput);
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.pendingRequests = new ConcurrentHashMap<String, Payload>();
    this.listeners = new LinkedHashSet<ClientSocketListener>();
    this.running = true;
    this.latestMousePoint = MouseInfo.getPointerInfo().getLocation();
    ClientSocketServices.updateLastActiveTime();
    this.sendHeartbeat();
  }

  /**
   * Starts this {@code ClientSocket} in the separately executing thread.
   * The {@code ClientSocket} would repeatedly:
   * <ul>
   * <li>update the mouse movement location of the user
   * <li>determine whether a heartbeat payload should be sent
   * <li>try to write a payload to the server, if there are any in the global payload queue
   * <li>try to read and process a payload from the server, if there are any in the input stream
   * </ul>
   * Until {@code running} is set to false, which can be caused by:
   * <ul>
   * <li>if the socket is timed out
   * <li>if a SocketException is received
   * <li>if the public method .close() is called
   * </ul>
   */
  @Override
  public void run() {
    while (this.running) {
      this.updateMouseMovement();

      // send heartbeat
      if (ClientSocketServices.getLastActiveTimeMills() - this.lastHeartBeatTimeMills >= Constants.HEARTBEAT_FREQUENCY) {
        this.sendHeartbeat();
      }

      // send payload
      synchronized (GlobalPayloadQueue.queue) {
        if (GlobalPayloadQueue.queue.size() > 0) {
          try {
              Payload payloadToSend = GlobalPayloadQueue.queue.poll();
              if (payloadToSend.getType() != PayloadType.KEEP_ALIVE){
                System.out.println("---------putting payload");
                System.out.println(payloadToSend.toString());
                System.out.println(payloadToSend.getType());
                System.out.println(payloadToSend.getId());
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
      
      // read and process payload
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

    // Closes the socket connection as well as
    // all input/output streams in this {@code ClientSocket}.
    try {
      this.socket.close();
      this.input.close();
      this.output.close();
    } catch (SocketException socketException) {
      // notify listeners
      this.notifyRequestStatus(PayloadType.KEEP_ALIVE, false, "You have been disconnected");
    } catch (IOException ioException) {
      System.out.println("An error has occurred!");
      ioException.printStackTrace();
    }
  }

  /**
   * Informs the {@code ClientSocket} to terminate.
   */
  public void terminate() {
    this.running = false;
  }

  /**
   * Adds a {@code ClientSocketListener} to listen for the events of this {@code ClientSocket}.
   * @param listener The {@code ClientSocketListener} to be added.
   */
  public synchronized void addListener(ClientSocketListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Removes a {@code ClientSocketListener} from this {@code ClientSocket}'s listeners.
   * @param listener The {@code ClientSocketListener} to be removed.
   */
  public synchronized void removeListener(ClientSocketListener listener) {
    this.listeners.remove(listener);
  }

  /**
   * Processes a given payload according to its type.
   * @param payload The payload to be processed.
   */
  private synchronized void processPayload(Payload payload) {
    switch (payload.getType()) {
      // response from client_to_server payloads
      case CLIENT_REQUEST_STATUS:
        this.processRequestStatus((ClientRequestStatus)payload);
        break;

      // server_to_client payloads
      case ATTACHMENT_TO_CLIENT:
        AttachmentToClient attachmentPayload = (AttachmentToClient)payload;
        Attachment attachment = attachmentPayload.getAttachment();
        this.saveAttachment(attachment);
        break;

      case CLIENT_CHANNELS_UPDATE:
        ClientChannelsUpdate channelsUpdate = (ClientChannelsUpdate)payload;
        GlobalClient.clientData.setChannels(channelsUpdate.getChannels());
        this.notifyClientDataUpdate();
        break;

      case CLIENT_FRIENDS_UPDATE:
        ClientFriendsUpdate friendsUpdate = (ClientFriendsUpdate)payload;
        GlobalClient.clientData.setFriends(friendsUpdate.getFriends());
        GlobalClient.clientData.setIncomingFriendRequests(friendsUpdate.getIncomingFriendRequests());
        GlobalClient.clientData.setOutgoingFriendRequests(friendsUpdate.getOutgoingFriendRequests());
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
        System.out.println("unknown payload type: " + payload);
        break;
    }
    System.out.println("processed payload " + payload);
  }

  /**
   * Adds and/or removes a message from the stored message history data.
   * <ul>
   * <li>When the message is new, adds it into the corresponding channel message history.
   * <li>When the message is edited, replaces the original message with the edited message,
   * if it exists in the local message history.
   * <li>When the message is removed, remove the original message,
   * if it exists in the local message history.
   * </ul>
   * @param messageUpdate The {@code MessageUpdateToClient} payload to be processed.
   */
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

  /**
   * Processes a request status returned from server.
   * @param requestStatus The {@code ClientRequestStatus} payload to be processed.
   */
  private synchronized void processRequestStatus(ClientRequestStatus requestStatus) {
    String originalPayloadId = requestStatus.getRequestPayloadId();
    Payload originalPayload = this.pendingRequests.get(originalPayloadId);
    String errorMessage = requestStatus.getErrorMessage();

    if (originalPayload == null) {
      System.out.println(originalPayloadId);
      System.out.println("client request originalPayload not found");

    } else if (errorMessage != null) {
      // notify listeners
      this.notifyRequestStatus(originalPayload.getType(), false, errorMessage);
      System.out.println("An error has occurred! (" + errorMessage + ")");

    } else {
      // error message is null: request successful
      // update data locally for certain types of payloads (profile, status)
      switch (originalPayload.getType()) {
        case CHANGE_PROFILE:
          this.changeProfile((ChangeProfile)originalPayload);
          break;

        case UPDATE_STATUS:
          System.out.println("hiiiiiiiiiiiiiiii");
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
    // remove the original payload from pending requests
    this.pendingRequests.remove(originalPayloadId);
    System.out.println(originalPayloadId);
  }

  /**
   * Attempts to download an attachment.
   * @param attachment The {@code Attachment} to be downloaded.
   */
  private synchronized void saveAttachment(Attachment attachment) {
    File directory = new File(GlobalClient.getDownloadFolderPath());
    if (!directory.exists()) {
      directory.mkdirs();
    }
    Path filePath = directory.toPath().resolve(attachment.getName());
    // if a file with the same name exists, rename it by adding a version number in the front
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
      // notify listeners
      this.notifyRequestStatus(
        PayloadType.REQUEST_ATTACHMENT,
        true,
        "Successfully downloaded " + attachment.getName() + " to " + filePath
      );
    } catch (IOException e) {
      e.printStackTrace();
      // notify listeners
      this.notifyRequestStatus(
        PayloadType.REQUEST_ATTACHMENT,
        false,
        "Failed to downloaded " + attachment.getName()
      );
    }
  }

  /**
   * Changes a profile field of the user.
   * @param changeProfile The {@code ChangeProfile} payload to be processed.
   */
  private synchronized void changeProfile(ChangeProfile changeProfile) {
    String newValue = changeProfile.getNewValue();
    switch (changeProfile.getFieldToChange()) {
      case USERNAME:
        GlobalClient.clientData.setUsername(newValue);
        this.notifyClientDataUpdate(); // notify listeners
        break;

      case DESCRIPTION:
        GlobalClient.clientData.setDescription(newValue);
        this.notifyClientDataUpdate(); // notify listeners
        break;
    }
  }

  /**
   * Updates the status of the user.
   * @param updateStatus The {@code UpdateStatus} payload to be processed.
   */
  private synchronized void updateUserStatus(UpdateStatus updateStatus) {
    GlobalClient.clientData.setStatus(updateStatus.getStatus());
    System.out.println("status:" + updateStatus.getStatus());
    this.notifyClientDataUpdate(); // notify listeners
  }

  /**
   * Notifies this {@code ClientSocket's} listeners about a change in the client's data.
   */
  private void notifyClientDataUpdate() {
    for (ClientSocketListener listener: this.listeners) {
      listener.clientDataUpdated();
    }
  }

  /**
   * Notifies this {@code ClientSocket's} listeners about a broadcast from the server.
   * @param broadcast The {@code ServerBroadcast} payload to notify about.
   */
  private synchronized void notifyServerBroadcast(ServerBroadcast broadcast) {
    for (ClientSocketListener listener: this.listeners) {
      listener.serverBroadcastReceived(broadcast);
    }
  }

  /**
   * Notifies this {@code ClientSocket's} listeners about a broadcast from the server.
   * @param broadcast The {@code ServerBroadcast} payload to be notified about.
   */
  private synchronized void sendHeartbeat() {
    GlobalPayloadQueue.enqueuePayload(new KeepAlive());
    this.lastHeartBeatTimeMills = System.currentTimeMillis();
  }

  /**
   * Notifies this {@code ClientSocket's} listeners about a client request's status.
   * @param payloadType   The type of the client request payload.
   * @param successful    Whether or not the request is successful.
   * @param notifMessage  The notification message for the listeners.
   */
  private synchronized void notifyRequestStatus(
    PayloadType payloadType,
    boolean successful,
    String notifMessage
  ) {
    for (ClientSocketListener listener: this.listeners) {
      listener.clientRequestStatusReceived(payloadType, successful, notifMessage);
    }
  }
  
  /**
   * Compares the current mouse location of the user with the latest mouse location.
   * If the two locations differ, the user's last active time is updated and the
   * latest mouse location would be updated with the current mouse location.
   */
  private void updateMouseMovement() {
    Point curPoint = MouseInfo.getPointerInfo().getLocation();
    if (
      (curPoint.getX() != this.latestMousePoint.getX())
      || (curPoint.getY() != this.latestMousePoint.getY())
    ) {
      ClientSocketServices.updateLastActiveTime();
      this.latestMousePoint = curPoint;
    }
  }

}
