package common.entities.payload;

import java.util.ArrayList;
import java.util.Hashtable;

import common.entities.*;
import server.entities.*;

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
  private final String token;
  private final ArrayList<UserMetadata> friends;
  private final ArrayList<UserMetadata> incomingFriendRequests;
  private final Hashtable<UserMetadata, FriendRequestStatus> outgoingFriendRequests;
  private final ArrayList<UserMetadata> blocked;
  private final ArrayList<ChannelMetadata> channels;

  public ClientInfo(
    int priority,
    String userId,
    String token, 
    ArrayList<UserMetadata> friends,
    ArrayList<UserMetadata> incomingFriendRequests,
    Hashtable<UserMetadata, FriendRequestStatus> outgoingFriendRequests,
    ArrayList<UserMetadata> blocked,
    ArrayList<ChannelMetadata> channels
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

  public String getToken() {
    return this.token;
  }

  public ArrayList<UserMetadata> getFriends() {
    return this.friends;
  }

  public ArrayList<UserMetadata> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public Hashtable<UserMetadata,FriendRequestStatus> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public ArrayList<UserMetadata> getBlocked() {
    return this.blocked;
  }

  public ArrayList<ChannelMetadata> getChannels() {
    return this.channels;
  }
}
