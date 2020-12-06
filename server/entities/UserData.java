package server.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import common.entities.Status;

public class UserData implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String userId;
  private String username;
  private String password;
  private ArrayList<String> friends;
  private ArrayList<String> incomingFriendRequests;
  private ArrayList<String> outgoingFriendRequests;
  private ArrayList<String> blocked;
  private ArrayList<String> channels;
  private Status status;

  public UserData(String username, String password) {
    this.username = username;
    this.password = password;
    this.userId = UUID.randomUUID().toString();
    this.setFriends(new ArrayList<>());
    this.setIncomingFriendRequests(new ArrayList<>());
    this.setOutgoingFriendRequests(new ArrayList<>());
    this.setBlocked(new ArrayList<>());
    this.setChannels(new ArrayList<>());
    this.setStatus(Status.ACTIVE);
  }

  public ArrayList<String> getFriends() {
    return friends;
  }

  public void setFriends(ArrayList<String> friends) {
    this.friends = friends;
  }

  public ArrayList<String> getIncomingFriendRequests() {
    return incomingFriendRequests;
  }

  public void setIncomingFriendRequests(ArrayList<String> incomingFriendRequests) {
    this.incomingFriendRequests = incomingFriendRequests;
  }

  public ArrayList<String> getOutgoingFriendRequests() {
    return outgoingFriendRequests;
  }

  public void setOutgoingFriendRequests(ArrayList<String> outgoingFriendRequests) {
    this.outgoingFriendRequests = outgoingFriendRequests;
  }

  public ArrayList<String> getBlocked() {
    return blocked;
  }

  public void setBlocked(ArrayList<String> blocked) {
    this.blocked = blocked;
  }

  public ArrayList<String> getChannels() {
    return channels;
  }

  public void setChannels(ArrayList<String> channels) {
    this.channels = channels;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
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

  public boolean hasPassword(String password) {
    return this.password.equals(password);
  }

  public void setPassword(String password) {
    this.password = password;
  }

  

}
