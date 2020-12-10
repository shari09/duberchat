package server.entities;

import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;

/**
 * A group channel where more two people can chat with each other.
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class GroupChannel extends Channel {
  private LinkedHashSet<UserMetadata> blacklist;
  private String ownerId;
  private String channelName;

  public GroupChannel(
    LinkedHashSet<UserMetadata> participants,
    String channelName,
    String ownerId
  ) {
    super(participants);
    this.ownerId = ownerId;
    this.channelName = channelName;
  }

  public synchronized LinkedHashSet<UserMetadata> getBlacklist() {
    return this.blacklist;
  }

  public synchronized void addToBlacklist(UserMetadata user) {
    this.blacklist.add(user);
  }

  public synchronized void removeFromBlacklist(UserMetadata user) {
    this.blacklist.remove(user);
  }

  public String getOwnerId() {
    return this.ownerId;
  }

  public String getChannelName() {
    return this.channelName;
  }

  public void updateChannelName(String name) {
    this.channelName = name;
  }

  public void updateOwner(String userId) {
    this.ownerId = userId;
  }

  public ChannelMetadata getMetadata() {
    return new ChannelMetadata(this.getId(), this.channelName);
  }

}
