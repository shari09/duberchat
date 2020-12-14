package server.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.ProfileField;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import server.entities.EventType;
import server.entities.User;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserService {
  private final String USERS_PATH = "database/data/users.ser";
  private final String USERNAME_MAPPING_PATH = "database/data/username-to-uid-mapping.ser";
  private final String USER_ID_MAPPING_PATH = "database/data/uid-to-username-mapping.ser";
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, String> usernameToId;
  private ConcurrentHashMap<String, String> idToUsername;
  private int numChanges = 0;
  private int bufferEntriesNum = 1;

  public UserService() {
    try {
      File usersFile = new File(this.USERS_PATH);
      if (!usersFile.exists()) {
        //log
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_LOG, 
          1,
          "Created new user save files"
        );
        this.users = new ConcurrentHashMap<>();
        this.usernameToId = new ConcurrentHashMap<>();
        this.idToUsername = new ConcurrentHashMap<>();
        this.hardSave();
        return;
      }

      this.users = DataService.loadData(this.USERS_PATH);
      this.usernameToId = DataService.loadData(this.USERNAME_MAPPING_PATH);
      this.idToUsername = DataService.loadData(this.USER_ID_MAPPING_PATH);

    } catch (Exception e) {
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        "Errors loading user save files"
      );
    }
  }

  public void save() {
    this.numChanges++;
    if (this.numChanges >= this.bufferEntriesNum) {
      this.hardSave();
      this.numChanges = 0;
    }
  }

  public synchronized void hardSave() {
    try {
      DataService.saveData(this.users, this.USERS_PATH);
      DataService.saveData(this.usernameToId, this.USERNAME_MAPPING_PATH);
      DataService.saveData(this.idToUsername, this.USER_ID_MAPPING_PATH);
    } catch (Exception e) {
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        "Errors saving user save files: " + e.getMessage()
      );
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public boolean usernameExist(String username) {
    return this.usernameToId.containsKey(username);
  }

  public String getUsername(String userId) {
    return this.idToUsername.get(userId);
  }

  public String getUserId(String username) {
    if (!this.usernameToId.containsKey(username)) {
      return null;
    }
    return this.usernameToId.get(username);
  }

  public boolean userIdExist(String userId) {
    return this.users.containsKey(userId);
  }

  /**
   * Changes a given user's password.
   * 
   * @param userId      the user's userId
   * @param oldPassword the user's old password
   * @param newPassword the user's desired new password
   * @return whether the change was a success
   */
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    if (!this.users.get(userId).hasPassword(oldPassword)) {
      return false;
    }
    this.users.get(userId).updatePassword(newPassword);
    this.save();
    return true;
  }

  public User authenticate(String username, String password) {
    if (this.usernameToId.containsKey(username)) {
      String userId = this.usernameToId.get(username);
      if (this.users.get(userId).hasPassword(password)) {
        return this.users.get(userId);
      }
      return null;
    }
    return null;
  }

  public boolean authenticateToken(String userId, Token token) {
    if (GlobalServices.tokens.get(userId).getValue().equals(token.getValue())) {
      return true;
    }
    return false;
  }

  public User newUser(String username, String password, String description) {
    if (this.usernameExist(username)) {
      return null;
    }
    User user = new User(username, password, description);
    this.users.put(user.getId(), user);
    this.usernameToId.put(username, user.getId());
    this.idToUsername.put(user.getId(), username);
    GlobalServices.serverEventQueue.emitEvent(EventType.NEW_USER, 1, user.getMetdata());
    this.save();
    return user;
  }

  public UserMetadata getUserMetadata(String userId) {
    if (!this.userIdExist(userId)) {
      return null;
    }
    return this.users.get(userId).getMetdata();
  }

  public boolean changeUsername(String userId, String newUsername) {
    if (this.usernameExist(newUsername)) {
      return false;
    }
    String oldUsername = this.idToUsername.get(userId);
    this.usernameToId.remove(oldUsername);
    this.idToUsername.put(userId, newUsername);
    this.usernameToId.put(newUsername, userId);
    this.broadcastChanges(this.users.get(userId));
    this.save();
    return true;
  }

  public void updateUserStatus(String userId, UserStatus status) {
    User user = this.users.get(userId);
    user.updateStatus(status);
    this.broadcastChanges(user);
  }

  /**
   * 
   * @param userId
   * @param recipientId
   * @param msg
   * @return                false if recipient doesn't exist or it's a duplicate
   */
  public boolean sendFriendRequest(String userId, String recipientId, String msg) {
    if (recipientId == null || !this.userIdExist(recipientId) || this.isBlocked(recipientId, userId)) {
      return false;
    }

    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    if (user.hasOutgoingFriendRequest(friend.getMetdata())
       || user.hasIncomingFriendRequest(friend.getMetdata())) {
      return false;
    }
    user.addOutgoingFriendRequest(friend.getMetdata(), msg);
    friend.addIncomingFriendRequest(user.getMetdata(), msg);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, friend);
    this.save();
    return true;
  }

  /**
   * 
   * @param blockerId
   * @param toBeBlockedId
   * @return                  whether the blocker blocked the blockee or not
   */
  public boolean isBlocked(String blockerId, String toBeBlockedId) {
    UserMetadata blockee = this.getUserMetadata(toBeBlockedId);
    return this.users.get(blockerId).getBlocked().contains(blockee);
  }

  public LinkedHashSet<ChannelMetadata> getChannels(String userId) {
    return this.users.get(userId).getChannels();
  }

  /**
   * 
   * @param userId
   * @param requesterId
   * @return false if the requester id doesn't exist
   */
  public boolean acceptFriendRequest(String userId, String requesterId) {
    if (!this.userIdExist(requesterId)) {
      return false;
    }

    User user = this.users.get(userId);
    User requester = this.users.get(requesterId);
    user.addFriend(requester.getMetdata());
    requester.addFriend(user.getMetdata());
    user.removeIncomingFriendRequest(requester.getMetdata());
    requester.removeOutgoingFriendRequest(user.getMetdata());
    ChannelMetadata channel = GlobalServices.channels.createPrivateChannel(
      user.getMetdata(),
      requester.getMetdata()
    );
    user.addChannel(channel);
    requester.addChannel(channel);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, requester);
    return true;
  }

  public void addChannel(String userId, ChannelMetadata channel) {
    User user = this.users.get(userId);
    user.addChannel(channel);
    this.save();
  }

  public void leaveChannel(String userId, ChannelMetadata channel) {
    this.users.get(userId).removeChannel(channel);
  }

  /**
   * 
   * @param userId
   * @param requesterId
   * @return false if the requester doesn't exist
   */
  public boolean rejectFriendRequest(String userId, String requesterId) {
    if (!this.userIdExist(requesterId)) {
      return false;
    }

    User user = this.users.get(userId);
    User requester = this.users.get(requesterId);
    user.removeIncomingFriendRequest(requester.getMetdata());
    requester.removeOutgoingFriendRequest(user.getMetdata());
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, requester);
    this.save();
    return true;
  }

  /**
   * 
   * @param userId
   * @param recipientId
   * @return false if the recipient doesn't exist
   */
  public boolean cancelFriendRequest(String userId, String recipientId) {
    if (!this.userIdExist(recipientId)) {
      return false;
    }

    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    user.removeOutgoingFriendRequest(friend.getMetdata());
    friend.removeIncomingFriendRequest(user.getMetdata());
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, friend);
    this.save();
    return true;
  }

  public void updateDescription(String userId, String description) {
    User user = this.users.get(userId);
    user.updateDescription(description);
    this.save();
  }

  public void removeFriend(String userId, String friendId) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendId);
    if (friend == null) {
      return;
    }
    user.removeFriend(friend.getMetdata());
    friend.removeFriend(friend.getMetdata());
    this.save();
  }

  public boolean isFriend(String userA, String userB) {
    if (!this.userIdExist(userB)) {
      return false;
    }
    return this.users.get(userA).hasFriend(this.users.get(userB).getMetdata());
  }

  /**
   * 
   * @param blockerId
   * @param blockeeUsername
   * @return false if the blockee doesn't exist
   */
  public boolean blockUser(String blockerId, String blockeeUsername) {
    if (!this.usernameExist(blockeeUsername)) {
      return false;
    }
    User user = this.users.get(blockerId);
    User blocked = this.users.get(this.usernameToId.get(blockeeUsername));
    user.removeFriend(blocked.getMetdata());
    blocked.removeFriend(user.getMetdata());
    user.addBlocked(blocked.getMetdata());
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, blocked);
    this.save();
    return true;
  }


  public boolean changeDescription(String userId, String description) {
    User user = this.users.get(userId);
    user.updateDescription(description);
    this.broadcastChanges(user);
    this.save();

    return true;
  }

  /**
   * 
   * @param user
   */
  private void broadcastChanges(User user) {
    Iterator<ChannelMetadata> channelsItr = user.getChannels().iterator();
    while (channelsItr.hasNext()) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.CHANNEL_UPDATE, 
        1, 
        channelsItr.next()
      );
    }

    Iterator<UserMetadata> itr = user.getFriends().iterator();
    while (itr.hasNext()) {
      User friend = this.users.get(itr.next().getUserId());
      GlobalServices.serverEventQueue.emitEvent(
        EventType.FRIEND_UPDATE, 
        1, 
        friend
      );
    }
  }

  public ArrayList<UserMetadata> getAllUsers() {
    ArrayList<UserMetadata> users = new ArrayList<>();
    for (User user: this.users.values()) {
      users.add(user.getMetdata());
    }
    return users;
  }

}
