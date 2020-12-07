package common.entities.payload;

import common.entities.*;

/**
 * A payload from client to server that
 * contains the data for a user to send a friend request to another user.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class FriendRequest extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String recipientName;
  private final String requestMessage;

  public FriendRequest(
    int priority,
    String userId,
    Token token,
    String recipientName,
    String requestMessage
  ) {
    super(
      PayloadType.FRIEND_REQUEST,
      priority,
      userId,
      token
    );

    this.recipientName = recipientName;
    this.requestMessage = requestMessage;
  }

  public String getRecipientName() {
    return this.recipientName;
  }

  public String getRequestMessage() {
    return this.requestMessage;
  }
  
}
