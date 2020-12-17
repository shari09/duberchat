package client.resources;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import common.entities.ClientData;
import common.entities.Message;
import common.entities.UserMetadata;

/**
 * Stores the data of the client.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalClient {
  /** The path to the root folder of downloads. */
  private static final String DOWNLOAD_ROOT_FOLDER_PATH = "downloads/";
  /** The default path to store downloaded data in downloads. */
  private static final String DEFAULT_DOWNLOAD_FOLDER_PATH = "guest/";
  /** The {@code ClientData} of this client. */
  public static ClientData clientData = null;
  /** The messages data the client has, with channel ids as keys. */
  public static ConcurrentHashMap<String, ConcurrentSkipListSet<Message>> messagesData = new ConcurrentHashMap<String, ConcurrentSkipListSet<Message>>();
  /** Whether or not the channels' message history is fully loaded, with channel ids as keys. */
  public static ConcurrentHashMap<String, Boolean> messageHistoryFullyLoaded = new ConcurrentHashMap<String, Boolean>();
  /**
   * Returns whether or not the client data of the {@code GlobalClient} is initialized.
   * @return Whether or not the client data of the {@code GlobalClient} is null.
   */
  public static boolean hasData() {
    return GlobalClient.clientData != null;
  }

  /**
   * Returns a {@code UserMetadata} that contains the client's user metadata.
   * @return A {@code UserMetadata} that contains the client's user metadata.
   */
  public static UserMetadata getClientUserMetadata() {
    synchronized (clientData) {
      return new UserMetadata(
        clientData.getUserId(), 
        clientData.getUsername(),
        clientData.getDescription(),
        clientData.getStatus()
      );
    }
  }
  
  /**
   * Returns the path to the download folder for the client.
   * If the client is signed in, the folder path is downloads/userId;
   * otherwise, it is downloads/guest.
   * @return A String representation of the path to the download folder for the client.
   */
  public static String getDownloadFolderPath() {
    if (!GlobalClient.hasData()) {
      return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.DEFAULT_DOWNLOAD_FOLDER_PATH;
    }
    return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.clientData.getUserId() + "/";
  }

  /**
   * Returns whether or not the client is friends with the given userId.
   * @param userId The user id of the other user.
   * @return true if the client is friends with the given userId, false otherwise.
   */
  public static synchronized boolean hasFriend(String userId) {
    LinkedHashSet<UserMetadata> friends = GlobalClient.clientData.getFriends();
    for (UserMetadata friend: friends) {
      if (friend.getUserId().equals(userId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns whether or not the client blocked the given userId.
   * @param userId The user id of the other user.
   * @return true if the client blocked the given userId, false otherwise.
   */
  public static synchronized boolean hasBlocked(String userId) {
    LinkedHashSet<UserMetadata> blockedUsers = GlobalClient.clientData.getBlocked();
    for (UserMetadata blocked: blockedUsers) {
      if (blocked.getUserId().equals(userId)) {
        return true;
      }
    }
    return false;
  }

}