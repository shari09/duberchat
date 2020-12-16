package client.services;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.LinkedHashSet;
import java.sql.Timestamp;

import client.resources.GlobalClient;
import common.entities.UserMetadata;
import common.entities.Message;
import common.entities.ChannelMetadata;
import common.entities.PrivateChannelMetadata;
import common.entities.GroupChannelMetadata;

/**
 * [description]
 * <p>
 * Created on 2020.12.13.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChannelServices {

  public static synchronized ChannelMetadata getChannelByChannelId(String channelId) {
    for (ChannelMetadata channelMetadata: GlobalClient.clientData.getChannels()) {
      if (channelMetadata.getChannelId().equals(channelId)) {
        return channelMetadata;
      }
    }
    return null;
  }

  public static synchronized void addMessages(String channelId, Message[] messages) {
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
    if (channelMessages == null) {
      GlobalClient.messagesData.put(channelId, new ConcurrentSkipListSet<Message>());
      channelMessages = GlobalClient.messagesData.get(channelId);
    }
    for (Message msg: messages) {
      if (msg != null) {
        channelMessages.add(msg);
        System.out.println("message: " + msg + "added");
      }
    }
  }

  public static synchronized void removeMessages(String channelId, Message[] messages) {
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
    if (channelMessages != null) {
      for (Message msg: messages) {
        if (msg != null) {
          channelMessages.remove(msg);
        }
      }
    }
  }

  public static synchronized String getChannelTitle(String channelId) {
    String title = "";
    ChannelMetadata channelMetadata = ChannelServices.getChannelByChannelId(channelId);
    
    if (channelMetadata instanceof PrivateChannelMetadata) {
      PrivateChannelMetadata pcMeta = ((PrivateChannelMetadata)channelMetadata);
      title = ChannelServices.getOtherUserInPrivateChannel(pcMeta).getUsername();

    } else if (channelMetadata instanceof GroupChannelMetadata) {
      title = ((GroupChannelMetadata)channelMetadata).getChannelName();
    }
    return title;
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
