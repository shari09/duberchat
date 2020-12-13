package common.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [description]
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientData implements Serializable {
  
  private static final long serialVersionUID = 1L;

  private String userId;
  private Token token;
  private String username;
  private String description;
  private UserStatus status;
  private LinkedHashSet<UserMetadata> friends;
  private ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private LinkedHashSet<UserMetadata> blocked;
  private LinkedHashSet<ChannelMetadata> channels;

  public ClientData(
    String userId,
    Token token,
    String username,
    String description,
    UserStatus status,
    LinkedHashSet<UserMetadata> friends,
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequests,
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests,
    LinkedHashSet<UserMetadata> blocked,
    LinkedHashSet<ChannelMetadata> channels
  ) {
    this.userId = userId;
    this.token = token;
    this.username = username;
    this.description = description;
    this.status = status;
    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.blocked = blocked;
    this.channels = channels;
  }

  public synchronized UserMetadata getBlockedByUserId(String userId) {
    for (UserMetadata blockedMetadata: this.blocked) {
      if (blockedMetadata.getUserId().equals(userId)) {
        return blockedMetadata;
      }
    }
    return null;
  }

  public synchronized ChannelMetadata getChannelByChannelId(String channelId) {
    for (ChannelMetadata channelMetadata: this.channels) {
      if (channelMetadata.getChannelId().equals(channelId)) {
        return channelMetadata;
      }
    }
    return null;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Token getToken() {
    return this.token;
  }

  public void setToken(Token token) {
    this.token = token;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public synchronized LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }

  public synchronized void setFriends(LinkedHashSet<UserMetadata> friends) {
    this.friends = friends;
  }

  public ConcurrentHashMap<UserMetadata, String> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public void setIncomingFriendRequests(ConcurrentHashMap<UserMetadata, String> incomingFriendRequests) {
    this.incomingFriendRequests = incomingFriendRequests;
  }

  public ConcurrentHashMap<UserMetadata, String> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public void setOutgoingFriendRequests(ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests) {
    this.outgoingFriendRequests = outgoingFriendRequests;
  }

  public synchronized LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public synchronized void setBlocked(LinkedHashSet<UserMetadata> blocked) {
    this.blocked = blocked;
  }

  public synchronized LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }

  public synchronized void setChannels(LinkedHashSet<ChannelMetadata> channels) {
    this.channels = channels;
    System.out.println(channels);
  }

}
