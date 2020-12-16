package common.entities.payload.server_to_client;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.UserMetadata;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from server to client that
 * contains a\n update on the user's friends.
 * <ul>
 * <li> sent new friend request
 * <li> cancelled friend request
 * <li> accepted friend request
 * <li> rejected friend request
 * </ul>
 * <p>
 * Created on 2020.12.09.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientFriendsUpdate extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final LinkedHashSet<UserMetadata> friends;
  private final ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private final ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private final LinkedHashSet<UserMetadata> blocked;

  /**
   * 
   * @param priority
   * @param friends
   * @param incomingFriendRequests
   * @param outgoingFriendRequests
   */
  public ClientFriendsUpdate(
    int priority,
    LinkedHashSet<UserMetadata> friends,
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequests,
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests,
    LinkedHashSet<UserMetadata> blocked
  ) {
    super(PayloadType.CLIENT_FRIENDS_UPDATE, priority);

    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.blocked = blocked;
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


}
