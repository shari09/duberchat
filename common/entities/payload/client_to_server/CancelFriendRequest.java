package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;


/**
 * A payload from client to server
 * that contains the data for cancelling an outgoing friend request.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class CancelFriendRequest extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String recipientId;
  
  public CancelFriendRequest(
    int priority,
    String userId,
    Token token,
    String recipientId
  ) {
    super(PayloadType.CANCEL_FRIEND_REQUEST, 1, userId, token);
    this.recipientId = recipientId;
  }

  public String getRecipientId() {
    return this.recipientId;
  }

}
