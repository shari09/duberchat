package server.entities;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import common.entities.ChannelMetadata;
import common.entities.Message;
import common.entities.UserMetadata;

/**
 * A channel that allows users to send messages in.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.1
 * @since 1.1.0
 */

public abstract class Channel {
  private String channelId;
  private LinkedHashSet<UserMetadata> participants;
  //TODO: blame java I can't get elements from a set
  private ConcurrentSkipListMap<Message, Message> messages;
  private ConcurrentHashMap<String, Message> idToMsgMapping;


  public Channel(
    LinkedHashSet<UserMetadata> participants
  ) {
    this.channelId = UUID.randomUUID().toString();;
    this.participants = participants;
    this.messages = new ConcurrentSkipListMap<>();
    this.idToMsgMapping = new ConcurrentHashMap<>();
  }

  public String getChannelId() {
    return this.channelId;
  }

  public synchronized LinkedHashSet<UserMetadata> getParticipants() {
    return this.participants;
  }

  public synchronized boolean addParticipant(UserMetadata user) {
    this.participants.add(user);
    return true;
  }

  public synchronized boolean removeParticipant(UserMetadata user) {
    this.participants.remove(user);
    return true;
  }

  public ChannelMetadata getMetadata() {
    return new ChannelMetadata(this.channelId, null);
  }

  public Message addMessage(Message message) {
    this.messages.put(message, message);
    this.idToMsgMapping.put(message.getMessageId(), message);
    return message;
  }

  public Message removeMessage(String messageId) {
    this.messages.remove(this.idToMsgMapping.get(messageId));
    return this.idToMsgMapping.remove(messageId);
  }

  public Message editMessage(String messageId, String content) {
    Message msg = this.messages.get(this.idToMsgMapping.get(messageId));
    msg.updateContent(content);
    return msg;
  }

  //TODO: improve time complexity???? but java only gives me itr from head/tail
  /**
   * Gets the specified number of messages before a certain time.
   * @param before        the specified time to query
   * @param numMessages   the number of messages to get
   * @return              the messages, some elements may be null if they don't exist
   */
  public Message[] getMessages(Timestamp before, int numMessages) {
    Iterator<Map.Entry<Message, Message>> itr = this.messages
                                                    .entrySet()
                                                    .iterator();
    Message[] msgs = new Message[numMessages];
    int i = 0;
    while (itr.hasNext()) {
      if (i == numMessages) {
        return msgs;
      }
      Message curMsg = itr.next().getValue();
      if (curMsg.getCreated().compareTo(before) <= 0) {
        msgs[i++] = curMsg;
      }
    }
    return msgs;
  }

}
