package client.services;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;

import client.resources.GlobalClient;
import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.Message;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;

/**
 * Contains static methods to retrieve or update channel information of the user.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChannelServices {
  /**
   * Returns a {@code ChannelMetadata} object of the channel with the given channel id.
   * @param channelId The id of the channel.
   * @return A {@code ChannelMetadata} object of the channel with the given channel id,
   *         or null if the user is not in such channel.
   */
  public static synchronized ChannelMetadata getChannelByChannelId(String channelId) {
    for (ChannelMetadata channelMetadata: GlobalClient.clientData.getChannels()) {
      if (channelMetadata.getChannelId().equals(channelId)) {
        System.out.println(channelMetadata.getParticipants().iterator().next().getStatus());
        return channelMetadata;
      }
    }
    return null;
  }

  /**
   * Loads an array of messages to the channel with the given channel id,
   * and determines whether or not the channel has more messages to request for.
   * @param channelId The id of the channel.
   * @param messages  The messages to be loaded.
   */
  public static synchronized void addMessages(String channelId, Message[] messages) {
    boolean fullyLoaded = false;
    ConcurrentSkipListSet<Message> channelMessages = GlobalClient.messagesData.get(channelId);
    if (channelMessages == null) {
      GlobalClient.messagesData.put(channelId, new ConcurrentSkipListSet<Message>());
      channelMessages = GlobalClient.messagesData.get(channelId);
    }
    for (Message msg: messages) {
      if (msg != null) {
        channelMessages.add(msg);
      } else {
        // if there are any nulls, the history is fully added and the channel does not need to further request
        fullyLoaded = true; 
      }
    }
    GlobalClient.messageHistoryFullyLoaded.put(channelId, fullyLoaded);
  }

  /**
   * Removes an array of messages from the channel with the given channel id.
   * @param channelId The id of the channel.
   * @param messages  The messages to be removed.
   */
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

  /**
   * Returns the channel's title.
   * If the channel is a private channel, the title would be the other user's username.
   * If the channel is a group channel, the title would be the channel's name.
   * @return A string representation of the channel's title.
   */
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

  /**
   * Returns the {@code UserMetadata} of the other user in the given private channel.
   * @param privateChannelMetadata  The given private channel.
   * @return The {@code UserMetadata} of the other user in the channel.
   */
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

  /**
   * Returns the timestamp of the earliest message in the channel with the given channel id,
   * among the messages in this channel that are stored in the client's message data.
   * @param channelId  The id of the channel.
   * @return The timestamp of the earliest message in the channel with the given channel id,
   *         among the messages in this channel that are stored in the client's message data.
   */
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
