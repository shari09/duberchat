package client.services;

import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.UserMetadata;
import client.resources.GlobalClient;

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

  public static synchronized boolean isOwned(String channelId) {
    String userId = GlobalClient.clientData.getUserId();
    LinkedHashSet<ChannelMetadata> channels = GlobalClient.clientData.getChannels();
    for (ChannelMetadata channel: channels) {
      if (channel instanceof GroupChannelMetadata) {
        if(((GroupChannelMetadata)channel).getOwnerId().equals(userId)) {
          return true;
        }
      }
    }
    return false;
  }
}
