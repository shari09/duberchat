package common.entities.payload;

import server.entities.*;

/**
 * A payload from client to server that
 * contains the data for a user's response to a friend request from another user.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class FriendRequestResponse extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String recipientId;
  private final boolean accepted;
  
  public FriendRequestResponse(
    int priority,
    String userId,
    Token token,
    String recipientId,
    boolean accepted
  ) {
    super(
      PayloadType.FRIEND_REQUEST_RESPONSE,
      priority,
      userId,
      token
    );

    this.recipientId = recipientId;
    this.accepted = accepted;
  }

  public String getRecipientId() {
    return this.recipientId;
  }

  public boolean isAccepted(){
    return this.accepted;
  }
  
}
