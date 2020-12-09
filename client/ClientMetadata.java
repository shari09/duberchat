package client;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.UserStatus;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.payload.ClientInfo;

/**
 * [description]
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientMetadata {
  private String userId;
  private Token token;
  private String username;
  private String password;
  private String description;
  private UserStatus status;
  private LinkedHashSet<UserMetadata> friends;
  private ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private LinkedHashSet<UserMetadata> blocked;
  private LinkedHashSet<ChannelMetadata> channels;

  public ClientMetadata(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public ClientMetadata(
    String userId,
    Token token,
    String username,
    String password,
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
    this.password = password;
    this.description = description;
    this.status = status;
    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.blocked = blocked;
    this.channels = channels;
  }

  public ClientMetadata(
    ClientInfo info,
    String username,
    String password
  ) {
    this.userId = info.getUserId();
    this.token = info.getToken();
    this.username = username;
    this.password = password;
    this.description = info.getDescription();
    this.friends = info.getFriends();
    this.incomingFriendRequests = info.getIncomingFriendRequests();
    this.outgoingFriendRequests = info.getOutgoingFriendRequests();
    this.blocked = info.getBlocked();
    this.channels = info.getChannels();
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

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public void setFriends(LinkedHashSet<UserMetadata> friends) {
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

  public LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public void setBlocked(LinkedHashSet<UserMetadata> blocked) {
    this.blocked = blocked;
  }

  public LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }

  public void setChannels(LinkedHashSet<ChannelMetadata> channels) {
    this.channels = channels;
  }

}
