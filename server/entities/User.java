package server.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.Identifiable;
import common.entities.UserMetadata;
import common.entities.UserStatus;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Creates a {@code User} data object that has getters/setters
 * and can be saved to database.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class User implements Serializable, Identifiable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  private String userId;
  private String username;
  private String password;
  private String description;
  private UserStatus status;
  private UserMetadata metadata;

  private LinkedHashSet<UserMetadata> friends;
  /** the user requesting and their requesting message */
  private ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private LinkedHashSet<UserMetadata> blocked;

  private LinkedHashSet<ChannelMetadata> channels;

  public User(String username, String password, String description) {
    this.userId = UUID.randomUUID().toString();
    this.username = username;
    this.password = password;
    this.description = description;
    this.status = UserStatus.ACTIVE;
    this.metadata = new UserMetadata(
      this.userId, 
      this.username, 
      this.description, 
      this.status
    );

    this.friends = new LinkedHashSet<>();
    
    this.incomingFriendRequests = new ConcurrentHashMap<>();
    this.outgoingFriendRequests = new ConcurrentHashMap<>();
    this.blocked = new LinkedHashSet<>();

    this.channels = new LinkedHashSet<>();
  }

  @Override
  public String getId() {
    return this.userId;
  }

  public String getUsername() {
    return this.username;
  }

  public void updateUsername(String username) {
    this.username = username;
    this.metadata.updateUsername(username);
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
    this.metadata.updateDescription(description);
  }
  
  public UserStatus getStatus() {
    return this.status;
  }

  public void updateStatus(UserStatus status) {
    this.status = status;
    this.metadata.updateStatus(status);
  }  

  public synchronized LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }

  public synchronized boolean hasFriend(UserMetadata user) {
    return this.friends.contains(user);
  }
  
  public synchronized void addFriend(UserMetadata user) {
    this.friends.add(user);
  }

  public synchronized void removeFriend(UserMetadata user) {
    this.friends.remove(user);
  }
  
  public ConcurrentHashMap<UserMetadata, String> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public boolean hasIncomingFriendRequest(UserMetadata user) {
    return this.incomingFriendRequests.containsKey(user);
  }

  public boolean hasOutgoingFriendRequest(UserMetadata user) {
    return this.outgoingFriendRequests.containsKey(user);
  }

  public void addIncomingFriendRequest(UserMetadata user, String msg) {
    this.incomingFriendRequests.put(user, msg);
  }

  public synchronized void removeIncomingFriendRequest(UserMetadata user) {
    this.incomingFriendRequests.remove(user);
  }

  public ConcurrentHashMap<UserMetadata, String> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public void addOutgoingFriendRequest(UserMetadata user, String msg) {
    this.outgoingFriendRequests.put(user, msg);
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
    return this.metadata;
  }

}
