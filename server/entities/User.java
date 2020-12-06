package server.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import common.entities.*;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class User implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  private String userId;
  private String username;
  private String password;
  private String description;
  private UserStatus status;

  private LinkedHashSet<String> friends;
  private LinkedHashSet<String> incomingFriendRequests;
  private ConcurrentHashMap<String, FriendRequestStatus> outgoingFriendRequests;
  private LinkedHashSet<String> blocked;

  private LinkedHashSet<String> channels;

  public User(String username, String password, String description) {
    this.userId = UUID.randomUUID().toString();
    this.username = username;
    this.password = password;
    this.description = description;
    this.status = UserStatus.ACTIVE;

    this.friends = new LinkedHashSet<String>();
    
    this.incomingFriendRequests = new LinkedHashSet<String>();
    this.outgoingFriendRequests = new ConcurrentHashMap<String, FriendRequestStatus>();
    this.blocked = new LinkedHashSet<String>();

    this.channels = new LinkedHashSet<String>();
  }

  public String getUserId() {
    return this.userId;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean hasPassword(String password) {
    return this.password.equals(password);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public UserStatus getStatus() {
    return this.status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }  

  public LinkedHashSet<String> getFriends() {
    return this.friends;
  }
  
  public void addFriend(String userId) {
    this.friends.add(userId);
  }

  public void removeFriend(String userId) {
    this.friends.remove(userId);
  }
  
  public LinkedHashSet<String> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public void addIncomingFriendRequest(String userId) {
    this.incomingFriendRequests.add(userId);
  }

  public void removeIncomingFriendRequest(String userId) {
    this.incomingFriendRequests.remove(userId);
  }

  public ConcurrentHashMap<String,FriendRequestStatus> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public void addOutgoingFriendRequest(String userId) {
    this.outgoingFriendRequests.put(userId, FriendRequestStatus.PENDING);
  }

  public void updateOutgoingFriendRequest(String userId, FriendRequestStatus status) {
    this.outgoingFriendRequests.put(userId, status);
  }

  public LinkedHashSet<String> getBlocked() {
    return this.blocked;
  }

  public void addBlocked(String userId) {
    this.blocked.add(userId);
  }

  public void removeBlocked(String userId) {
    this.blocked.remove(userId);
  }

  public LinkedHashSet<String> getChannels() {
    return this.channels;
  }
  
  public void addChannel(String channelId) {
    this.channels.add(channelId);
  }
  
  public void removeChannel(String channelId) {
    this.channels.remove(channelId);
  }

}
