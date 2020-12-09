package server.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;

import java.util.UUID;


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

  private LinkedHashSet<UserMetadata> friends;
  private LinkedHashSet<UserMetadata> incomingFriendRequests;
  private LinkedHashSet<UserMetadata> outgoingFriendRequests;
  private LinkedHashSet<UserMetadata> blocked;

  private LinkedHashSet<ChannelMetadata> channels;

  public User(String username, String password, String description) {
    this.userId = UUID.randomUUID().toString();
    this.username = username;
    this.password = password;
    this.description = description;
    this.status = UserStatus.ACTIVE;

    this.friends = new LinkedHashSet<>();
    
    this.incomingFriendRequests = new LinkedHashSet<>();
    this.outgoingFriendRequests = new LinkedHashSet<>();
    this.blocked = new LinkedHashSet<>();

    this.channels = new LinkedHashSet<>();
  }

  public String getUserId() {
    return this.userId;
  }

  public String getUsername() {
    return this.username;
  }

  public void updateUsername(String username) {
    this.username = username;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public boolean hasPassword(String password) {
    return this.password.equals(password);
  }

  public String getDescription() {
    return this.description;
  }

  public void updateDescription(String description) {
    this.description = description;
  }
  
  public UserStatus getStatus() {
    return this.status;
  }

  public void updateStatus(UserStatus status) {
    this.status = status;
  }  

  public synchronized LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }
  
  public synchronized void addFriend(UserMetadata user) {
    this.friends.add(user);
  }

  public synchronized void removeFriend(UserMetadata user) {
    this.friends.remove(user);
  }
  
  public synchronized LinkedHashSet<UserMetadata> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public synchronized void addIncomingFriendRequest(UserMetadata user) {
    this.incomingFriendRequests.add(user);
  }

  public synchronized void removeIncomingFriendRequest(UserMetadata user) {
    this.incomingFriendRequests.remove(user);
  }

  public synchronized LinkedHashSet<UserMetadata> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public synchronized void addOutgoingFriendRequest(UserMetadata user) {
    this.outgoingFriendRequests.add(user);
  }

  public synchronized void removeOutgoingFriendRequest(UserMetadata user) {
    this.outgoingFriendRequests.remove(user);
  }

  public synchronized LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public synchronized void addBlocked(UserMetadata user) {
    this.blocked.add(user);
  }

  public synchronized void removeBlocked(UserMetadata user) {
    this.blocked.remove(user);
  }

  public synchronized LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }
  
  public synchronized void addChannel(ChannelMetadata channel) {
    this.channels.add(channel);
  }
  
  public synchronized void removeChannel(ChannelMetadata channel) {
    this.channels.remove(channel);
  }

  public UserMetadata getMetdata() {
    return new UserMetadata(this.userId, this.username, this.status);
  }

}
