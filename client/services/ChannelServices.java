package client.services;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.LinkedHashSet;
import java.sql.Timestamp;

import client.resources.GlobalClient;
import common.entities.UserMetadata;
import common.entities.Message;
import common.entities.ChannelMetadata;
import common.entities.PrivateChannelMetadata;

/**
 * [description]
 * <p>
 * Created on 2020.12.13.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChannelServices {

  public static ChannelMetadata getChannelByChannelId(String channelId) {
    synchronized (GlobalClient.clientData) {
      for (ChannelMetadata channelMetadata: GlobalClient.clientData.getChannels()) {
        if (channelMetadata.getChannelId().equals(channelId)) {
          return channelMetadata;
        }
      }
    }
    return null;
  }

  public static void addMessages(String channelId, Message[] messages) {
    synchronized (GlobalClient.clientData) {
      ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
      // if (channelMessages != null) {
        for (Message msg: messages) {
          System.out.println("message: " + msg);
          if (msg != null) {
            channelMessages.add(msg);
          }
        }
      // }
    }
  }

  public static void removeMessages(String channelId, Message[] messages) {
    synchronized (GlobalClient.clientData) {
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
      if (channelMessages != null) {
        for (Message msg: messages) {
          if (msg != null) {
            channelMessages.remove(msg);
          }
        }
      }
    }
  }

  public static UserMetadata getOtherUserInPrivateChannel(PrivateChannelMetadata privateChannelMetadata) {
    LinkedHashSet<UserMetadata> participants = privateChannelMetadata.getParticipants();
    synchronized (GlobalClient.clientData) {
      for (UserMetadata userMetadata: participants) {
        if (!userMetadata.getUserId().equals(GlobalClient.clientData.getUserId())) {
          return userMetadata;
        }
      }
    }
    return null;
  }

  public static Timestamp getEarliestStoredMessageTime(String channelId) {
    synchronized (GlobalClient.clientData) {
      ConcurrentSkipListSet<Message> messages = GlobalClient.messagesData.get(channelId);
      if ((messages == null) || (messages.size() == 0)) {
        return null;
      } else {
        return messages.first().getCreated();
      }
    }
  }
}
