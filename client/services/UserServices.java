package client.services;

import java.util.LinkedHashSet;

import common.entities.UserMetadata;
import client.resources.GlobalClient;

/**
 * [description]
 * <p>
 * Created on 2020.12.13.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserServices {
  public static synchronized boolean isFriend(String userId) {
    LinkedHashSet<UserMetadata> friends = GlobalClient.clientData.getFriends();
    for (UserMetadata friend: friends) {
      if (friend.getUserId().equals(userId)) {
        return true;
      }
    }
    return false;
  }

  public static synchronized boolean isBlocked(String userId) {
    LinkedHashSet<UserMetadata> blockedUsers = GlobalClient.clientData.getBlocked();
    for (UserMetadata blocked: blockedUsers) {
      if (blocked.getUserId().equals(userId)) {
        return true;
      }
    }
    return false;
  }
}
