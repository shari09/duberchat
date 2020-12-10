package common.entities.payload;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;

/**
 * A payload from server to client that
 * contains the metadata of a user.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientInfo extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String userId;
  private final Token token;
  private final UserStatus status;
  private final String description;
  private final LinkedHashSet<UserMetadata> friends;
  private final ConcurrentHashMap<UserMetadata, String> incomingFriendRequests;
  private final ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests;
  private final LinkedHashSet<UserMetadata> blocked;
  private final LinkedHashSet<ChannelMetadata> channels;

  public ClientInfo(
    int priority,
    String userId,
    Token token, 
    UserStatus status,
    String description,
    LinkedHashSet<UserMetadata> friends,
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequests,
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequests,
    LinkedHashSet<UserMetadata> blocked,
    LinkedHashSet<ChannelMetadata> channels
  ) {
    super(PayloadType.CLIENT_FRIENDS_UPDATE, priority);

    this.userId = userId;
    this.token = token;
    this.status = status;
    this.description = description;
    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.blocked = blocked;
    this.channels = channels;
  }

  public String getUserId() {
    return this.userId;
  }

  public Token getToken() {
    return this.token;
  }

  public UserStatus getStatus() {
    return this.status;
  }

  public String getDescription() {
    return this.description;
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
