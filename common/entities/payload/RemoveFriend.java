package common.entities.payload;

import common.entities.Token;


/**
 * Removes a friend
 * <p>
 * Created on 2020.12.13.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemoveFriend extends AuthenticatablePayload {
  
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String friendId;
  
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
