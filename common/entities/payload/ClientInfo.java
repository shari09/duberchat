package common.entities.payload;

import java.util.LinkedHashSet;

import common.entities.*;

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
  private final LinkedHashSet<UserMetadata> friends;
  private final LinkedHashSet<UserMetadata> incomingFriendRequests;
  private final LinkedHashSet<UserMetadata> outgoingFriendRequests;
  private final LinkedHashSet<UserMetadata> blocked;
  private final LinkedHashSet<ChannelMetadata> channels;

  public ClientInfo(
    int priority,
    String userId,
    Token token, 
    LinkedHashSet<UserMetadata> friends,
    LinkedHashSet<UserMetadata> incomingFriendRequests,
    LinkedHashSet<UserMetadata> outgoingFriendRequests,
    LinkedHashSet<UserMetadata> blocked,
    LinkedHashSet<ChannelMetadata> channels
  ) {
    super(PayloadType.CLIENT_INFO, priority);

    this.userId = userId;
    this.token = token;
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

  public LinkedHashSet<UserMetadata> getFriends() {
    return this.friends;
  }

  public LinkedHashSet<UserMetadata> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public LinkedHashSet<UserMetadata> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public LinkedHashSet<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }
}
