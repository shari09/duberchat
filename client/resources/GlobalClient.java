package client.resources;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import common.entities.ClientData;
import common.entities.Message;
import common.entities.UserMetadata;
import common.entities.ChannelMetadata;

/**
 * [description]
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalClient {
  private static final String DOWNLOAD_ROOT_FOLDER_PATH = "downloads/";
  private static final String DEFAULT_DOWNLOAD_FOLDER_PATH = "guest/";
  
  public static ClientData clientData = null;
  
  public static ConcurrentHashMap<String, ConcurrentSkipListSet<Message>> messagesData = new ConcurrentHashMap<>();
  
  public static boolean hasData() {
    return GlobalClient.clientData != null;
  }

  public static String getDownloadFolderPath() {
    if (!GlobalClient.hasData()) {
      return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.DEFAULT_DOWNLOAD_FOLDER_PATH;
    }
    return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.clientData.getUserId() + "/";
  }

  public static void displayClientData() {
    if (!hasData()) {
      System.out.println("global client has not been initialized");
    } else {
      System.out.println("displaying client info");
      System.out.println("user id: " + clientData.getUserId());
      System.out.println("token: " + clientData.getToken().getValue());
      System.out.println("username: " + clientData.getUsername());
      System.out.println("description: " + clientData.getDescription());
      System.out.println("status: " + clientData.getStatus());
      System.out.println(Integer.toString(clientData.getFriends().size()) + " friends");
      System.out.println(Integer.toString(clientData.getIncomingFriendRequests().size()) + " incomingFriendRequests");
      System.out.println(Integer.toString(clientData.getOutgoingFriendRequests().size()) + " outgoingFriendRequests");
      System.out.println(Integer.toString(clientData.getBlocked().size()) + " blocked");
      System.out.println(Integer.toString(clientData.getChannels().size()) + " channels");
    }
  }

  public static void displayUserMetadata(UserMetadata metadata) {
    if (metadata == null) {
      System.out.println("UserMetadata is null");
    } else {
      System.out.println("displaying UserMetadata");
      System.out.println("user id: " + metadata.getUserId());
      System.out.println("username: " + metadata.getUsername());
      System.out.println("description: " + metadata.getDescription());
      System.out.println("status: " + metadata.getStatus());
    }
  }

  public static void displayChannelMetadata(ChannelMetadata metadata) {
    if (metadata == null) {
      System.out.println("ChannelMetadata is null");
    } else {
      System.out.println("displaying ChannelMetadata");
      System.out.println("channel id: " + metadata.getChannelId());
      System.out.println("last modified: " + metadata.getLastModified().toString());
      System.out.println(Integer.toString(metadata.getParticipants().size()) + " participants");
    }
  }
}
