package server.services;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelField;
import common.entities.ChannelMetadata;
import common.entities.Message;
import common.entities.UserMetadata;
import common.entities.Attachment;
import server.entities.Channel;
import server.entities.EventType;
import server.entities.GroupChannel;
import server.entities.LogType;
import server.entities.PrivateChannel;

/**
 * A collection of methods that deal with messaging and channel services.
 * <ul>
 * <li> send attachment
 * <li> save attachment
 * <li> get/update channel
 * <li> add/edit/remove messages to/from a channel
 * <li> get the message history from save files
 * <li> create private/group channels
 * <li> add/remove/blacklist participants
 * <li> leave channels
 * <li> transfer group channel ownerships
 * <li> change channel profile settings
 * </ul>
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessagingService {
  private final String CHANNELS_DIR_PATH = "database/channels/";
  private final String ASSETS_DIR_PATH = "database/assets/";
  private ConcurrentHashMap<String, Channel> channels;
  /** The number of changes made to channels */
  private ConcurrentHashMap<String, Integer> numChanges;
  /**
   * Number of changes before a save operation.
   * <p>
   * It is set at 1 right now (save on every change) 
   * because properly data buffering to prevent data loss is not implemented. 
   * However, this is useful in the long run.
   */
  private final int bufferEntriesNum = 1;

  public MessagingService() {
    this.channels = new ConcurrentHashMap<>();
    this.numChanges = new ConcurrentHashMap<>();
  }

  /**
   * Checks for the number of modification on that channel
   * and decide whether or not to save it.
   * @param channelId      the channel to save
   */
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

  /**
   * Saving the specified channel data.
   * The data is saved based on the channel ID under 
   * {@link MessagingService#CHANNELS_DIR_PATH}.
   * @param channelId       the channel to save
   */
  public synchronized void hardSave(String channelId) {
    try {
      String path = this.CHANNELS_DIR_PATH + channelId + ".ser";
      new File(path).getParentFile().mkdirs();
      DataService.saveData(this.getChannel(channelId), path);
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Saving channel data: %s \n%s", 
        e.getMessage(), 
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
  }

  /**
   * Saves an attachment file and return the ID.
   * 
   * @param name the name of the attachment
   * @param data the data of the attachment file
   * @return     the attachment ID
   */
  private String saveAttatchment(String name, byte[] data) {
    Attachment attachment = new Attachment(name, data);
    DataService.saveData(attachment, this.ASSETS_DIR_PATH + attachment.getId() + ".ser");
    return attachment.getId();
  }

  /**
   * Gets the channel.
   * <p>
   * If the channel exist in cache, it will load from 
   * {@code MessagingService#channels}. Otherwise, it 
   * will attempt to read from the save file.
   * @param channelId    the channel ID
   * @return             the channel/null
   */
  private Channel getChannel(String channelId) {
    if (this.channels.containsKey(channelId)) {
      return this.channels.get(channelId);
    }
    String filePath = this.CHANNELS_DIR_PATH + channelId + ".ser";
    Channel channel = DataService.loadData(filePath);
    this.channels.put(channelId, channel);
    return channel;    
  }

  /**
   * Adding a message to a channel.
   * @param channelId    the channel ID
   * @param message      the message
   */
  private void addMsgToChannel(String channelId, Message message) {
    Channel channel = this.getChannel(channelId);
    if (channel == null) {
      return;
    }
    channel.addMessage(message);
    this.save(channelId);
  }

  /**
   * If it's a private channel, and one of the users block the other,
   * they can't send messages.
   * For group channels, if they are blacklisted, 
   * they also can't send messages
   * @param senderId     the sender's user ID
   * @param channelId    the channel ID
   * @return             whether they successfully sent the message or not
   */
  public boolean allowMessaging(String senderId, String channelId) {
    Channel channel = this.getChannel(channelId);
    if (channel instanceof PrivateChannel) {
      LinkedHashSet<UserMetadata> participants = ((PrivateChannel)channel).getParticipants();
      for (UserMetadata user: participants) {
        if (!user.getUserId().equals(senderId)) {
          if (
            GlobalServices.users.isBlocked(user.getUserId(), senderId)
            || GlobalServices.users.isBlocked(senderId, user.getUserId())
          ) {
            return false;
          }
        }
      }
      return true;
    }
    
    GroupChannel gc = (GroupChannel)channel;
    return !gc.isBlacklisted(GlobalServices.users.getUserMetadata(senderId));

  }

  /**
   * Add a message to a specific channel.
   * The message may or may not have a file attached to it.
   * If so, it will save the attachment in total database and 
   * return an attachment ID so when a user requets to download,
   * the server can send back the original attachment.
   * 
   * @param senderId           the sender's user ID
   * @param channelId          the channel ID
   * @param content            the message content
   * @param attachment         the attachment/null
   * @param attachmentName     the attachment name/null
   * @return                   successfully sent or not
   */
  public boolean addMessage(
    String senderId, 
    String channelId, 
    String content, 
    byte[] attachment,
    String attachmentName
  ) {
    if (this.getChannel(channelId) == null || !this.allowMessaging(senderId, channelId)) {
      return false;
    }
    Message msg;
    if (attachment != null) {
      String attachmentId = this.saveAttatchment(attachmentName, attachment);
      msg = new Message(content, senderId, channelId, attachmentId, attachmentName);
    } else {
      msg = new Message(content, senderId, channelId, null, null);
    }

    this.addMsgToChannel(channelId, msg);
    GlobalServices.serverEventQueue.emitEvent(EventType.NEW_MESSAGE, 1, msg);
    return true;
  }

  /**
   * Get a portion of the message history.
   * @param channelId          the channel ID
   * @param before             request messages before this time
   * @param numMessages        the number of messages to load
   * @return                   the messages/null if channel does not exist
   */
  public Message[] getMessages(String channelId, Timestamp before, int numMessages) {
    Channel channel = this.getChannel(channelId);
    if (channel == null) {
      return null;
    }
    Message[] messages = channel.getMessages(before, numMessages);
    return messages;
  }

  /**
   * Gets a requested attachement from the database.
   * @param attachmentId  the attachment ID
   * @return              the attachment/null
   * @see                 MessagingService#addMessage(String, String, String, byte[], String)
   */
  public Attachment getAttachment(String attachmentId) {
    return DataService.loadData(this.ASSETS_DIR_PATH + attachmentId + ".ser");
  }

  /**
   * Removes a message.
   * @param channelId      the channel ID
   * @param messageId      the message ID
   */
  public void removeMessage(String channelId, String messageId) {
    Message msg = this.getChannel(channelId).removeMessage(messageId);
    this.save(channelId);
    GlobalServices.serverEventQueue.emitEvent(EventType.REMOVE_MESSAGE, 1, msg);
  }

  /**
   * Edits a message.
   * @param channelId       the channel ID
   * @param messageId       the message ID
   * @param newContent      the new content
   */
  public void editMessage(String channelId, String messageId, String newContent) {
    Channel channel = this.getChannel(channelId);
    Message msg = channel.editMessage(messageId, newContent);
    this.save(channelId);
    GlobalServices.serverEventQueue.emitEvent(EventType.EDIT_MESSAGE, 1, msg);
  }

  /**
   * Checks if a user is the message sender or not.
   * @param userId            the user ID
   * @param channelId         the channel ID
   * @param messageId         the message ID
   * @return                  whether or not the user is the message sender
   */
  public boolean isMessageSender(String userId, String channelId, String messageId) {
    Channel channel = this.getChannel(channelId);
    if (channel.isMessageSender(userId, messageId)) {
      return true;
    }
    return false;
  }

  /**
   * Creates a private channel between two users and notify both users
   * about the new channel.
   * <p>
   * Emits a {@code CHANNEL_UPDATE} event.
   * @param userOne         the first user
   * @param userTwo         the second user
   * @return                the {@link ChannelMetadata} of the new channel
   * @see                   PrivateChannel
   */
  public ChannelMetadata createPrivateChannel(
    UserMetadata userOne, 
    UserMetadata userTwo
  ) {
    PrivateChannel channel = new PrivateChannel(userOne, userTwo);
    this.channels.put(channel.getId(), channel);
    this.hardSave(channel.getId());
    GlobalServices.users.addChannel(
      userOne.getUserId(), 
      channel.getMetadata()
    );
    GlobalServices.users.addChannel(
      userTwo.getUserId(), 
      channel.getMetadata()
    );
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 1, channel.getMetadata()
    );
    return channel.getMetadata();
  }

  /**
   * Creates a group channel given a set of initial participants
   * that the owner (user) selected.
   * 
   * <p>
   * Emits a {@code CHANNEL_UPDATE} event.
   * @param participants    a set of participants the user added
   * @param channelName     the channel name
   * @param ownerId         the owner's user ID
   * @return                the {@linke ChannelMetadata} of the new channel
   * @see                   GroupChannel
   */ 
  public ChannelMetadata createGroupChannel(
    LinkedHashSet<UserMetadata> participants, 
    String channelName,
    String ownerId
  ) {    
    
    GroupChannel channel = new GroupChannel(participants, channelName, ownerId);
    for (UserMetadata user: participants) {
      GlobalServices.users.addChannel(
        user.getUserId(), 
        channel.getMetadata()
      ); 
    }
    this.channels.put(channel.getId(), channel);
    this.hardSave(channel.getId());
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1, 
      channel.getMetadata()
    );
    return channel.getMetadata();
  }

  /**
   * Gets the participants of a channel.
   * @param channelId   the channel ID
   * @return            a set of the channel's participants
   * @see               UserMetadata
   */
  public LinkedHashSet<UserMetadata> getParticipants(String channelId) {
    return this.getChannel(channelId).getParticipants();
  }

  /**
   * Add a selected participant to the channel.
   * If the participant is blacklisted, they will not be able to be added.
   * 
   * <p>
   * Emits a {@code CHANNEL_UPDATE} event.
   * @param userId           the to-be-added user's ID
   * @param channelId        the channel ID
   * @return                 whether the user was added successfully
   */
  public boolean addParticipant(String userId, String channelId) {
    GroupChannel channel = (GroupChannel)this.getChannel(channelId);
    if (channel.isBlacklisted(GlobalServices.users.getUserMetadata(userId))) {
      return false;
    }
    channel.addParticipant(GlobalServices.users.getUserMetadata(userId));
    GlobalServices.users.addChannel(userId, channel.getMetadata());
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1, 
      channel.getMetadata()
    );
    this.save(channelId);
    return true;
  }

  /**
   * Removes a participant from a channel. 
   * The user attempting this action must be the owner of the group channel.
   * @param userId       the user attempting the removal 
   * @param removedId    the user that is being removed
   * @param channelId    the channel ID
   * @return             whether the participant is successfully removed
   */
  public boolean removeParticipant(
    String userId, 
    String removedId,
    String channelId
  ) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }

    GroupChannel gc = (GroupChannel) this.getChannel(channelId);
    gc.removeParticipant(GlobalServices.users.getUserMetadata(removedId));
    
    GlobalServices.users.leaveChannel(removedId, gc.getMetadata());
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1, 
      gc.getMetadata()
    );
    GlobalServices.serverEventQueue.emitEvent(
      EventType.LEFT_CHANNEL, 
      1, 
      GlobalServices.users.getUserMetadata(removedId)
    );
    this.save(channelId);
    return true;
  }

  /**
   * Checks whether the user has admin permissions over a channel.
   * If it's a private channel, no users have admin permission over that.
   * @param userId          the user ID
   * @param channelId       the channel ID
   * @return                whether the user has admin permission
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
   * Blacklist a user from a channel.
   * If the user is not the owner, they do not have permission to do so.
   * @param userId            the user blacklisting the participant
   * @param blacklistedId     the user being blacklisted
   * @param channelId         the channel ID
   * @return                  whether the user is blacklisted
   */
  public boolean blacklistUser(String userId, String blacklistedId, String channelId) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }
    GroupChannel channel = (GroupChannel)this.getChannel(channelId);
    UserMetadata user = GlobalServices.users.getUserMetadata(blacklistedId);
    channel.removeParticipant(user);
    GlobalServices.users.leaveChannel(blacklistedId, channel.getMetadata());
    channel.addToBlacklist(GlobalServices.users.getUserMetadata(blacklistedId));
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1, 
      channel.getMetadata()
    );
    this.save(channelId);
    return true;
  }

  /**
   * A user leaving a channel.
   * <p>
   * If the user leaving the channel is the owner of the channel,
   * the ownership will be transferred to a random participant
   * unless there is no one else, in which case the channel will delete
   * itself after the owner leaves.
   * @param userId      the user ID
   * @param channelId   the channel ID
   * @return            whether the user successfully leaves the channel
   */
  public boolean leaveChannel(String userId, String channelId) {
    if (this.getChannel(channelId) instanceof PrivateChannel) {
      return false;
    }
    GroupChannel channel = (GroupChannel) this.getChannel(channelId);
    channel.removeParticipant(GlobalServices.users.getUserMetadata(userId));
    
    GlobalServices.users.leaveChannel(userId, channel.getMetadata());
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 
      1, 
      channel.getMetadata()
    );
    GlobalServices.serverEventQueue.emitEvent(
      EventType.LEFT_CHANNEL, 
      1, 
      GlobalServices.users.getUserMetadata(userId)
    );
    this.save(channelId);


    //owner

    if (!channel.getOwnerId().equals(userId)) {
      return true;
    }
    GlobalServices.users.leaveChannel(userId, channel.getMetadata());
    // if the group chat is empty
    if (channel.getSize() == 0) {
      this.channels.remove(channelId);
      return true;
    }

    // assign random owner
    channel.updateOwner(channel.getParticipants().iterator().next().getUserId());
    this.save(channelId);
    return true;
  }

  /**
   * The owner is allowed to transfer their ownership to another
   * participant in the channel.
   * @param userId           the user ID
   * @param recipientId      the recipient ID
   * @param channelId        the channel ID
   * @return                 whether the ownersip is successfully transferred
   */
  public boolean transferOwnership(String userId, String recipientId, String channelId) {
    if (!this.hasAdminPermission(userId, channelId)) {
      return false;
    }

    GroupChannel gc = (GroupChannel) this.getChannel(channelId);
    gc.updateOwner(recipientId);
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 1, gc.getMetadata()
    );
    
    this.save(channelId);
    return true;
  }

  /**
   * Change the channel profiles such as channel name.
   * @param channelId        the channel ID
   * @param fieldToChange    the field to change {@link ChannelField}
   * @param newValue         the new value to set to
   * @return                 whether the value is successfully changed
   */
  public boolean changeChannelSettings(
    String channelId, 
    ChannelField fieldToChange, 
    String newValue
  ) {
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
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CHANNEL_UPDATE, 1, gc.getMetadata()
    );
    this.save(channelId);
    return true;
  }
}
