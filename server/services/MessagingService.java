package server.services;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.Attachment;
import common.entities.ChannelMetadata;
import common.entities.Message;
import common.entities.UserMetadata;
import server.entities.Channel;
import server.entities.EventType;
import server.entities.GroupChannel;
import server.entities.PrivateChannel;
import server.resources.GlobalEventQueue;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun
<<<<<<< HEAD
 * @version 1.0.1
 * @since 1.0.2
=======
 * @version 1.0.0
 * @since 1.0.0
>>>>>>> 7a25c22a9ed78d1284104337731193e52c18bbbe
 */
public class MessagingService {
  private final String CHANNELS_DIR_PATH = "cliendata/data/channels/";
  private final String ASSETS_DIR_PATH = "cliendata/assets/";
  private ConcurrentHashMap<String, Channel> channels;
  private ConcurrentHashMap<String, Integer> numChanges;
  private final int bufferEntriesNum = 2;

  public MessagingService() {
    this.numChanges = new ConcurrentHashMap<>();
  }

  public void save(String channelId) {
    if (this.numChanges.containsKey(channelId)) {
      this.numChanges.put(channelId, this.numChanges.get(channelId));
    } else {
      this.numChanges.put(channelId, 1);
    }
    
    if (this.numChanges.get(channelId) >= this.bufferEntriesNum) {
      this.hardSave(channelId);
      this.numChanges.put(channelId, 0);
    }

  }

  public synchronized void hardSave(String channelId) {
    try {
      String path = this.CHANNELS_DIR_PATH+channelId+".ser";
      new File(path).getParentFile().mkdirs();
      DataService.saveData(this.channels.get(channelId), path);
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Saves an attachment file and return the ID.
   * @param name    the name of the attachment
   * @param data    the data of the attachment file
   * @return        the attachment ID
   */
  private String saveAttatchment(String name, byte[] data) {
    Attachment attachment = new Attachment(name, data);
    DataService.saveData(
      attachment, 
      this.ASSETS_DIR_PATH+attachment.getId()+".ser"
    );
    return attachment.getId();
  }

  /**
   * 
   * @param channelId
   * @return               the channel/null
   */
  private Channel getChannel(String channelId) {
    if (this.channels.containsKey(channelId)) {
      return this.channels.get(channelId);
    }
    String filePath = this.CHANNELS_DIR_PATH+channelId+".ser";
    return DataService.loadData(filePath);    
  }

  private void updateChannel(String channelId, Channel channel) {
    this.channels.put(channelId, channel);
    this.save(channelId);
  }

  private void updateChannel(String channelId) {
    this.save(channelId);
  }

  private void addMsgToChannel(String channelId, Message message) {
    Channel channel = getChannel(channelId);
    if (channel == null) {
      System.out.println(channelId + " channel doesn't exist");
      return;
    }
    channel.addMessage(message);
    this.updateChannel(channelId, channel);
  }

  /**
   * Add a message to a specific channel.
   * @param senderId
   * @param channelId
   * @param content
   * @param attachment
   * @param attachmentName
   * @return                   successfully sent or not
   */
  public boolean addMessage(
    String senderId, 
    String channelId,
    String content, 
    byte[] attachment,
    String attachmentName
  ) {
    if (this.getChannel(channelId) == null) {
      return false;
    }
    Message msg;
    if (attachment == null) {
      String attachmentId = this.saveAttatchment(attachmentName, attachment);
      msg = new Message(
        content, 
        senderId, 
        channelId, 
        attachmentId, 
        attachmentName
      );
    } else {
      msg = new Message(content, senderId, channelId, null, null);
    }

    this.addMsgToChannel(channelId, msg);
    GlobalEventQueue.queue.emitEvent(EventType.NEW_MESSAGE, 1, msg);
    return true;
  }


  /**
   * 
   * @param channelId
   * @param before
   * @param numMessages
   * @return                the messages/null
   */
  public Message[] getMessages(
    String channelId, 
    Timestamp before, 
    int numMessages
  ) {
    Channel channel = getChannel(channelId);
    if (channel == null) {
      return null;
    }
    Message[] messages = channel.getMessages(before, numMessages);
    return messages;
  }

  /**
   * 
   * @param attachmentId
   * @return               the attachment/null
   */
  public Attachment getAttachment(String attachmentId) {
    return DataService.loadData(this.ASSETS_DIR_PATH+attachmentId+".ser");
  }

  public void removeMessage(String channelId, String messageId) {
    Message msg = this.channels.get(channelId).removeMessage(messageId);
    this.updateChannel(channelId);
    GlobalEventQueue.queue.emitEvent(EventType.REMOVE_MESSAGE, 1, msg);
  }

  public void editMessage(
    String channelId, 
    String messageId, 
    String newContent
  ) {
    Channel channel = this.channels.get(channelId);
    Message msg = channel.editMessage(messageId, newContent);
    this.updateChannel(channelId);
    GlobalEventQueue.queue.emitEvent(EventType.EDIT_MESSAGE, 1, msg);
  }

  public ChannelMetadata createPrivateChannel(
    UserMetadata userOne, 
    UserMetadata userTwo
  ) {
    PrivateChannel channel = new PrivateChannel(userOne, userTwo);
    this.channels.put(channel.getChannelId(), channel);
    this.hardSave(channel.getChannelId());
    GlobalEventQueue.queue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1,
      channel
    );
    return channel.getMetadata();
  }

  /**
   * Creates a group channel and returns the metadata
   * @param participants
   * @param channelName
   * @param ownerId
   * @return               the channel's metadata
   */
  public ChannelMetadata createGroupChannel(
    LinkedHashSet<UserMetadata> participants,
    String channelName,
    String ownerId
  ) {
    //TODO: verify that all the participants exist
    GroupChannel channel = new GroupChannel(participants, channelName, ownerId);
    this.channels.put(channel.getChannelId(), channel);
    this.hardSave(channel.getChannelId());
    GlobalEventQueue.queue.emitEvent(EventType.CHANNEL_UPDATE, 1, channel);
    return channel.getMetadata();
  }

  public LinkedHashSet<UserMetadata> getParticipants(String channelId) {
    return this.channels.get(channelId).getParticipants();
  }
}
