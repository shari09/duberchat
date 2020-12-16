package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;


/**
 * A payload from client to server that
 * contains the data for a user to remove a friend.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class RemoveFriend extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String friendId;
  
  public RemoveFriend(
    int priority,
    String userId,
    Token token,
    String friendId
  ) {
    super(PayloadType.REMOVE_FRIEND, 1, userId, token);
    this.friendId = friendId;
  }

  public String getFriendId() {
    return this.friendId;
  }

}
