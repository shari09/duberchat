package common.entities.payload;

import common.entities.Token;


/**
 * Cancels an outgoing friend request
 * <p>
 * Created on 2020.12.13.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class CancelFriendRequest extends AuthenticatablePayload {
  
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String recipientId;
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
