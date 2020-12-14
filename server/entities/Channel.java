package server.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.Identifiable;
import common.entities.Message;
import common.entities.UserMetadata;

/**
 * A channel that allows users to send messages in.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.1.0
 */

public abstract class Channel implements Identifiable, Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String channelId;
  private LinkedHashSet<UserMetadata> participants;
  private ConcurrentSkipListMap<Message, Message> messages;
  private ConcurrentHashMap<String, Message> idToMsgMapping;
  private int size;
  private Timestamp lastModified;
  private ChannelMetadata metadata;

  public Channel(LinkedHashSet<UserMetadata> participants) {
    this.channelId = UUID.randomUUID().toString();
    this.size = participants.size();
    this.participants = participants;
    this.messages = new ConcurrentSkipListMap<>();
    this.idToMsgMapping = new ConcurrentHashMap<>();
    this.lastModified = new Timestamp(System.currentTimeMillis());
    this.metadata = this.getNewMetadata();
  }

  public Timestamp getLastModified() {
    return this.lastModified;
  }

  

  @Override
  public String getId() {
    return this.channelId;
  }

  public synchronized LinkedHashSet<UserMetadata> getParticipants() {
    return this.participants;
  }

  public synchronized boolean hasParticipant(UserMetadata user) {
    return this.participants.contains(user);
  }

  public synchronized boolean addParticipant(UserMetadata user) {
    this.participants.add(user);
    this.metadata.updateParticipants(this.participants);
    this.size++;
    return true;
  }

  public synchronized boolean removeParticipant(UserMetadata user) {
    this.participants.remove(user);
    this.metadata.updateParticipants(this.participants);
    this.size--;
    return true;
  }

  public int getSize() {
    return this.size;
  }

  public ChannelMetadata getMetadata() {
    return this.metadata;
  }

  public void updateMetadata(ChannelMetadata metadata) {
    this.metadata = metadata;
    if (this.metadata instanceof GroupChannelMetadata) {

      System.out.println("updated"+((GroupChannelMetadata)this.metadata).getOwnerId());
    }
  }

  /**
   * 
   * @return    a new metadata object
   */
  public abstract ChannelMetadata getNewMetadata();

  public Message addMessage(Message message) {
    this.messages.put(message, message);
    this.idToMsgMapping.put(message.getId(), message);
    this.lastModified = new Timestamp(System.currentTimeMillis());
    this.metadata.updateLastModified(this.lastModified);
    return message;
  }

  public Message removeMessage(String messageId) {
    this.messages.remove(this.idToMsgMapping.get(messageId));
    return this.idToMsgMapping.remove(messageId);
  }

  public Message editMessage(String messageId, String content) {
    Message msg = this.messages.get(this.idToMsgMapping.get(messageId));
    msg.updateContent(content);
    this.lastModified = new Timestamp(System.currentTimeMillis());
    this.metadata.updateLastModified(this.lastModified);
    return msg;
  }

  public boolean isMessageSender(String userId, String messageId) {
    return this.messages.get(this.idToMsgMapping.get(messageId))
                        .getSenderId()
                        .equals(userId);
  }

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
