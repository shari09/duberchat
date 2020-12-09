package common.entities.payload;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;

/**
 * An update on the user's friends.
 * <ul>
 * <li> sent new friend request
 * <li> cancelled friend request
 * <li> accepted friend request
 * <li> rejected friend request
 * <li> blocked (removed as friends) 
 * <li> new channel
 * <li> removed from channel
 * </ul>
 * <p>
 * Created on 2020.12.09.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClientInfoUpdate extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final UserStatus status;
  private final LinkedHashSet<UserMetadata> friends;
  private final ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private final ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private final LinkedHashSet<UserMetadata> blocked;
  private final LinkedHashSet<ChannelMetadata> channels;

  /**
   * 
   * @param priority
   * @param status
   * @param friends
   * @param incomingFriendRequests
   * @param outgoingFriendRequests
   * @param blocked
   * @param channels
   */
  public ClientInfoUpdate(
    int priority,
    UserStatus status,
    LinkedHashSet<UserMetadata> friends,
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequests,
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests,
    LinkedHashSet<UserMetadata> blocked,
    LinkedHashSet<ChannelMetadata> channels
  ) {
    super(PayloadType.CLIENT_INFO, priority);

    this.status = status;
    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.blocked = blocked;
    this.channels = channels;
  }

  public UserStatus getStatus() {
    return this.status;
  }

  public LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }

  public ConcurrentHashMap<UserMetadata, String> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public ConcurrentHashMap<UserMetadata, String> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }

}
