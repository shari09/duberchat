package server.services;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import server.entities.EventType;
import server.entities.FriendRequest;
import server.entities.User;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;
import server.resources.TempData;


/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserService {
  private final String USERS_PATH = "clientdata/data/users.ser";
  private final String USERNAME_MAPPING_PATH = "clientdata/data/username-to-uid-mapping.ser";
  private final String USER_ID_MAPPING_PATH = "clientdata/data/uid-to-username-mapping.ser";
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, String> usernameToUid;
  private ConcurrentHashMap<String, String> uidToUsername;
  private int numChanges = 0;
  private int bufferEntriesNum = 1;
  
  public UserService() {    
    try {
      File usersFile = new File(this.USERS_PATH);
      if (!usersFile.exists()) {
        System.out.println("Created new save files");
        this.users = new ConcurrentHashMap<>();
        this.usernameToUid = new ConcurrentHashMap<>();
        this.uidToUsername = new ConcurrentHashMap<>();
        this.hardSave();
        return;
      }

      this.users = DataService.loadData(this.USERS_PATH);
      this.usernameToUid = DataService.loadData(this.USERNAME_MAPPING_PATH);
      this.uidToUsername = DataService.loadData(this.USER_ID_MAPPING_PATH);

    } catch (Exception e) {
      System.out.println("Error loading the data");
    }
  }

  public void save() {
    if (this.numChanges >= this.bufferEntriesNum) {
      this.hardSave();
    }
    this.numChanges = 0;
  }

  public synchronized void hardSave() {
    try {
      DataService.saveData(this.users, this.USERS_PATH);
      DataService.saveData(this.usernameToUid, this.USERNAME_MAPPING_PATH);
      DataService.saveData(this.uidToUsername, this.USER_ID_MAPPING_PATH);
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public boolean usernameExist(String username) {
    return this.usernameToUid.containsKey(username);
  }

  public String getUsername(String userId) {
    return this.uidToUsername.get(userId);
  }

  public String getUserId(String username) {
    return this.usernameToUid.get(username);
  }

  public boolean userIdExist(String userId) {
    return this.users.containsKey(userId);
  }

  /**
   * Changes a given user's password.
   * @param userId          the user's userId
   * @param oldPassword     the user's old password
   * @param newPassword     the user's desired new password
   * @return                whether the change was a success
   */
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    if (!this.users.get(userId).hasPassword(oldPassword)) {
      return false;
    }
    this.users.get(userId).updatePassword(newPassword);
    this.numChanges++;
    this.save();
    return true;
  }

  public User authenticate(String username, String password) {
    if (this.usernameToUid.containsKey(username)) {
      String userId = this.usernameToUid.get(username);
      if (this.users.get(userId).hasPassword(password)) {
        return this.users.get(userId);
      }
      return null;
    }
    return null;
  }

  public boolean authenticateToken(String userId, Token token) {
    if (TempData.tokens.get(userId) == token) {
      return true;
    }
    return false;
  }

  public User add(String username, String password, String description) {
    if (this.usernameExist(username)) {
      return null;
    }
    User user = new User(username, password, description);
    this.users.put(user.getUserId(), user);
    this.usernameToUid.put(username, user.getUserId());
    this.uidToUsername.put(user.getUserId(), username);
    this.numChanges++;
    this.save();
    return user;
  }

  public UserMetadata getUserMetadata(String userId) {
    return this.users.get(userId).getMetdata();
  }

  /**
   * 
   * @param userId
   * @return           the user
   */
  public User getUser(String userId) {
    return this.users.get(userId);
  }

  public void changeUsername(String uid, String newUsername) {
    String oldUsername = this.uidToUsername.get(uid);
    this.usernameToUid.remove(oldUsername);
    this.uidToUsername.put(uid, newUsername);
    this.usernameToUid.put(newUsername, uid);
    this.numChanges++;
    this.save();
  }

  public void updateUserStatus(String userId, UserStatus status) {
    User user = this.users.get(userId);
    user.updateStatus(status);
  }


  /**
   * 
   * @param userId
   * @param recipientId
   * @param msg
   * @return               false if the recipient doesn't exist
   */
  public boolean sendFriendRequest(String userId, String recipientId, String msg) {
    if (!this.userIdExist(recipientId)) {
      return false;
    }    
    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    user.addOutgoingFriendRequest(friend.getMetdata(), msg);
    friend.addIncomingFriendRequest(user.getMetdata(), msg);
    GlobalEventQueue.queue.emitEvent(
      EventType.FRIEND_REQUEST, 
      1,
      new FriendRequest(userId, recipientId)
    );
    return true;
  }

  /**
   * 
   * @param blockerId
   * @param blockeeId
   * @return            whether the blocker blocked the blockee or not
   */
  public boolean isBlocked(String blockerId, String blockeeId) {
    UserMetadata blockee = this.getUserMetadata(blockeeId);
    return this.users.get(blockerId).getBlocked().contains(blockee);
  }

  /**
   * 
   * @param userId
   * @param requesterId
   * @return                 false if the requester id doesn't exist
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
    ChannelMetadata channel = StoredData.channels.createPrivateChannel(
      user.getMetdata(), 
      requester.getMetdata()
    );
    user.addChannel(channel);
    requester.addChannel(channel);
    GlobalEventQueue.queue.emitEvent(
      EventType.FRIEND_REQUEST, 
      1,
      new FriendRequest(userId, requesterId)
    );
    return true;
  }

  /**
   * 
   * @param userId
   * @param requesterId
   * @return              false if the requester doesn't exist
   */
  public boolean rejectFriendRequest(String userId, String requesterId) {
    if (!this.userIdExist(requesterId)) {
      return false;
    }
    
    User user = this.users.get(userId);
    User requester = this.users.get(requesterId);
    user.removeIncomingFriendRequest(requester.getMetdata());
    requester.removeOutgoingFriendRequest(user.getMetdata());
    GlobalEventQueue.queue.emitEvent(
      EventType.FRIEND_REQUEST, 
      1,
      new FriendRequest(userId, requesterId)
    );
    return true;
  }

  /**
   * 
   * @param userId
   * @param recipientId
   * @return              false if the recipient doesn't exist
   */
  public boolean cancelFriendRequest(String userId, String recipientId) {
    if (!this.userIdExist(recipientId)) {
      return false;
    }
    
    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    user.removeOutgoingFriendRequest(friend.getMetdata());
    friend.removeIncomingFriendRequest(user.getMetdata());
    GlobalEventQueue.queue.emitEvent(
      EventType.FRIEND_REQUEST, 
      1,
      new FriendRequest(userId, recipientId)
    );
    return true;
  }

  public void updateDescription(String userId, String description) {
    User user = this.users.get(userId);
    user.updateDescription(description);
  }

  public void removeFriend(String userId, UserMetadata friendMetatdata) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendMetatdata.getUserId());
    user.removeFriend(friendMetatdata);
    friend.removeFriend(friend.getMetdata());
  }

  /**
   * 
   * @param userId
   * @param blockedUsername
   * @return                    false if the blocked user doesn't exist
   */
  public boolean blockUser(String userId, String blockedUsername) {
    if (!this.usernameExist(blockedUsername)) {
      return false;
    }
    User user = this.users.get(userId);
    User blocked = this.users.get(this.usernameToUid.get(blockedUsername));
    user.removeFriend(blocked.getMetdata());
    blocked.removeFriend(user.getMetdata());
    user.addBlocked(blocked.getMetdata());
    return true;
  }
}
