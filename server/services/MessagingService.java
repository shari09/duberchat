package server.services;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.Attachment;
import common.entities.ChannelField;
import common.entities.ChannelMetadata;
import common.entities.Message;
import common.entities.UserMetadata;
import server.entities.Channel;
import server.entities.EventType;
import server.entities.GroupChannel;
import server.entities.PrivateChannel;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
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
      String path = this.CHANNELS_DIR_PATH + channelId + ".ser";
      new File(path).getParentFile().mkdirs();
      DataService.saveData(this.getChannel(channelId), path);
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Saves an attachment file and return the ID.
   * 
   * @param name the name of the attachment
   * @param data the data of the attachment file
   * @return the attachment ID
   */
  private String saveAttatchment(String name, byte[] data) {
    Attachment attachment = new Attachment(name, data);
    DataService.saveData(attachment, this.ASSETS_DIR_PATH + attachment.getId() + ".ser");
    return attachment.getId();
  }

  /**
   * 
   * @param channelId
   * @return the channel/null
   */
  private Channel getChannel(String channelId) {
    if (this.channels.containsKey(channelId)) {
      return this.channels.get(channelId);
    }
    String filePath = this.CHANNELS_DIR_PATH + channelId + ".ser";
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
   * 
   * @param senderId
   * @param channelId
   * @param content
   * @param attachment
   * @param attachmentName
   * @return successfully sent or not
   */
  public boolean addMessage(String senderId, String channelId, String content, byte[] attachment,
      String attachmentName) {
    if (this.getChannel(channelId) == null) {
      return false;
    }
    Message msg;
    if (attachment == null) {
      String attachmentId = this.saveAttatchment(attachmentName, attachment);
      msg = new Message(content, senderId, channelId, attachmentId, attachmentName);
    } else {
      msg = new Message(content, senderId, channelId, null, null);
    }

    this.addMsgToChannel(channelId, msg);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.NEW_MESSAGE, 1, msg);
    return true;
  }

  /**
   * 
   * @param channelId
   * @param before
   * @param numMessages
   * @return the messages/null
   */
  public Message[] getMessages(String channelId, Timestamp before, int numMessages) {
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
   * @return the attachment/null
   */
  public Attachment getAttachment(String attachmentId) {
    return DataService.loadData(this.ASSETS_DIR_PATH + attachmentId + ".ser");
  }

  /**
   * 
   * @param channelId
   * @param messageId
   */
  public void removeMessage(String channelId, String messageId) {
    Message msg = this.getChannel(channelId).removeMessage(messageId);
    this.updateChannel(channelId);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.REMOVE_MESSAGE, 1, msg);
  }

  // TODO: verify the existence of the message
  /**
   * 
   * @param channelId
   * @param messageId
   * @param newContent
   */
  public void editMessage(String channelId, String messageId, String newContent) {
    Channel channel = this.getChannel(channelId);
    Message msg = channel.editMessage(messageId, newContent);
    this.updateChannel(channelId);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.EDIT_MESSAGE, 1, msg);
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @param messageId
   * @return
   */
  public boolean isMessageSender(String userId, String channelId, String messageId) {
    Channel channel = this.getChannel(channelId);
    if (channel.isMessageSender(userId, messageId)) {
      return true;
    }
    return false;
  }

  /**
   * 
   * @param userOne
   * @param userTwo
   * @return
   */
  public ChannelMetadata createPrivateChannel(UserMetadata userOne, UserMetadata userTwo) {
    PrivateChannel channel = new PrivateChannel(userOne, userTwo);
    this.channels.put(channel.getId(), channel);
    this.hardSave(channel.getId());
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, channel);
    return channel.getMetadata();
  }

  /**
   * Creates a group channel and returns the metadata
   * 
   * @param participants
   * @param channelName
   * @param ownerId
   * @return the channel's metadata
   */
  public ChannelMetadata createGroupChannel(LinkedHashSet<UserMetadata> participants, String channelName,
      String ownerId) {
    // TODO: verify that all the participants exist
    GroupChannel channel = new GroupChannel(participants, channelName, ownerId);
    this.channels.put(channel.getId(), channel);
    this.hardSave(channel.getId());
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, channel.getMetadata());
    return channel.getMetadata();
  }

  /**
   * 
   * @param channelId
   * @return
   */
  public LinkedHashSet<UserMetadata> getParticipants(String channelId) {
    return this.getChannel(channelId).getParticipants();
  }

  /**
   * Emits a CHANNEL_UPDATE event
   * 
   * @param userId
   * @param channelId
   * @return if the user is blacklisted
   */
  public boolean addParticipant(String userId, String channelId) {
    Channel channel = this.getChannel(channelId);
    if (channel.isBlacklisted(userId)) {
      return false;
    }
    channel.addParticipant(GlobalServerServices.users.getUserMetadata(userId));
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, channel.getMetadata());
    return true;
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @return if the participant is removed or not
   */
  public boolean removeParticipant(String userId, String channelId) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }
    GroupChannel gc = (GroupChannel) this.getChannel(channelId);
    gc.removeParticipant(GlobalServerServices.users.getUserMetadata(userId));
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, gc.getMetadata());
    return true;
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @return whether the user/channel has admin permission
   */
  public boolean hasAdminPermission(String userId, String channelId) {
    Channel channel = this.getChannel(channelId);
    if (!this.channels.containsKey(channelId) || channel instanceof PrivateChannel) {
      return false;
    }
    GroupChannel gc = (GroupChannel) channel;
    if (!gc.getOwnerId().equals(userId)) {
      return false;
    }
    return true;
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @return whether the user is blacklisted or not
   */
  public boolean blacklistUser(String userId, String channelId) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }
    Channel channel = this.getChannel(channelId);
    channel.blacklistUser(userId);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, channel.getMetadata());
    return true;
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @return if the user successfully left or not
   */
  public boolean leaveChannel(String userId, String channelId) {
    if (this.getChannel(channelId) instanceof PrivateChannel) {
      return false;
    }
    GroupChannel channel = (GroupChannel) this.getChannel(channelId);
    this.removeParticipant(userId, channelId);
    if (!channel.getOwnerId().equals(userId)) {
      return true;
    }
    // if the group chat is empty
    if (channel.getSize() == 0) {
      this.channels.remove(channelId);
      return true;
    }

    // assign random owner
    channel.updateOwner(channel.getParticipants().iterator().next().getUserId());
    return true;
  }

  /**
   * 
   * @param userId
   * @param channelId
   * @return true if successfully transferred
   */
  public boolean transferOwnership(String userId, String channelId) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }

    GroupChannel gc = (GroupChannel) this.getChannel(channelId);
    gc.updateOwner(userId);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, gc.getMetadata());
    return true;
  }

  /**
   * 
   * @param channelId
   * @param fieldToChange
   * @param newValue
   * @return if the setting is successfully changed
   */
  public boolean changeChannelSettings(String channelId, ChannelField fieldToChange, String newValue) {
    Channel channel = this.getChannel(channelId);
    if (channel instanceof PrivateChannel) {
      return false;
    }
    GroupChannel gc = (GroupChannel) channel;
    switch (fieldToChange) {
      case NAME:
        gc.updateChannelName(newValue);
        break;
    }
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CHANNEL_UPDATE, 1, gc.getMetadata());
    return true;
  }
}
