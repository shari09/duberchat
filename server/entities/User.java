package server.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.FriendRequestStatus;
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
  private ConcurrentHashMap<UserMetadata, FriendRequestStatus> outgoingFriendRequests;
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
    this.outgoingFriendRequests = new ConcurrentHashMap<>();
    this.blocked = new LinkedHashSet<>();

    this.channels = new LinkedHashSet<>();
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

  public void updatePassword(String password) {
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

  public LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }
  
  public void addFriend(UserMetadata userMetadata) {
    this.friends.add(userMetadata);
  }

  public void removeFriend(UserMetadata userMetaData) {
    this.friends.remove(userMetaData);
  }
  
  public LinkedHashSet<UserMetadata> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public void addIncomingFriendRequest(UserMetadata userMetadata) {
    this.incomingFriendRequests.add(userMetadata);
  }

  public void removeIncomingFriendRequest(UserMetadata userMetadata) {
    this.incomingFriendRequests.remove(userMetadata);
  }

  public ConcurrentHashMap<UserMetadata,FriendRequestStatus> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public void addOutgoingFriendRequest(UserMetadata userMetadata) {
    this.outgoingFriendRequests.put(userMetadata, FriendRequestStatus.PENDING);
  }

  public void updateOutgoingFriendRequest(UserMetadata userMetadata, FriendRequestStatus status) {
    this.outgoingFriendRequests.put(userMetadata, status);
  }

  public LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public void addBlocked(UserMetadata userMetadata) {
    this.blocked.add(userMetadata);
  }

  public void removeBlocked(UserMetadata userMetadata) {
    this.blocked.remove(userMetadata);
  }

  public LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }
  
  public void addChannel(ChannelMetadata channelMetadata) {
    this.channels.add(channelMetadata);
  }
  
  public void removeChannel(ChannelMetadata channelMetadata) {
    this.channels.remove(channelMetadata);
  }

}
