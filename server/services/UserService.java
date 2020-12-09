package server.services;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import server.entities.User;
import server.resources.StoredData;
import server.resources.TempData;


/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.1.2
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

  public boolean usernameExists(String username) {
    return this.usernameToUid.containsKey(username);
  }

  public String getUsername(String userId) {
    System.out.println(userId);
    return this.uidToUsername.get(userId);
  }

  public String getUserId(String username) {
    return this.usernameToUid.get(username);
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
    if (this.usernameExists(username)) {
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

  public void sendFriendRequest(String userId, String recipientName) {
    User user = this.users.get(userId);
    User friend = this.users.get(this.usernameToUid.get(recipientName));
    user.addOutgoingFriendRequest(friend.getMetdata());
    friend.addIncomingFriendRequest(user.getMetdata());
  }

  public void acceptFriendRequest(String userId, UserMetadata friendMetadata) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendMetadata.getUserId());
    user.addFriend(friendMetadata);
    friend.addFriend(user.getMetdata());
    user.removeIncomingFriendRequest(friend.getMetdata());
    friend.removeOutgoingFriendRequest(user.getMetdata());
    ChannelMetadata channel = StoredData.channels.createPrivateChannel(
      user.getMetdata(), 
      friend.getMetdata()
    );
    user.addChannel(channel);
    friend.addChannel(channel);
  }

  public void rejectFriendRequest(String userId, UserMetadata friendMetadata) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendMetadata.getUserId());
    user.removeIncomingFriendRequest(friend.getMetdata());
    friend.removeOutgoingFriendRequest(user.getMetdata());
  }

  public void cancelFriendRequest(String userId, UserMetadata friendMetadata) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendMetadata.getUserId());
    user.removeOutgoingFriendRequest(friend.getMetdata());
    friend.removeIncomingFriendRequest(user.getMetdata());
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

  public void blockUser(String userId, String blockedUsername) {
    User user = this.users.get(userId);
    User blocked = this.users.get(this.usernameToUid.get(blockedUsername));
    user.removeFriend(blocked.getMetdata());
    blocked.removeFriend(user.getMetdata());
    user.addBlocked(blocked.getMetdata());
  }
}
