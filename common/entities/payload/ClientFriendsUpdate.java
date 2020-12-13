package common.entities.payload;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.UserMetadata;

/**
 * An update on the user's friends.
 * <ul>
 * <li> sent new friend request
 * <li> cancelled friend request
 * <li> accepted friend request
 * <li> rejected friend request
 * </ul>
 * <p>
 * Created on 2020.12.09.
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

  /**
   * 
   * @param priority
   * @param friends
   * @param incomingFriendRequests
   * @param outgoingFriendRequests
   */
  @SuppressWarnings("unchecked")
  public ClientFriendsUpdate(
    int priority,
    LinkedHashSet<UserMetadata> friends,
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequests,
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests
  ) {
    super(PayloadType.CLIENT_FRIENDS_UPDATE, priority);

    this.friends = (LinkedHashSet<UserMetadata>)friends.clone();
    this.incomingFriendRequests = new ConcurrentHashMap<UserMetadata, String>();
    this.outgoingFriendRequests = new ConcurrentHashMap<UserMetadata, String>();
    for (UserMetadata user: incomingFriendRequests.keySet()) {
      this.incomingFriendRequests.put(user, incomingFriendRequests.get(user));
    }
    for (UserMetadata user: outgoingFriendRequests.keySet()) {
      this.outgoingFriendRequests.put(user, outgoingFriendRequests.get(user));
    }
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


}
