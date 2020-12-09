package server.entities;

/**
 * Contains a friend request data.
 * <p>
 * Created on 2020.12.09.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class FriendRequest {
  private final String userId;
  private final String recipientId;


  public FriendRequest(String userId, String recipientId) {
    this.userId = userId;
    this.recipientId = recipientId;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getRecipientId() {
    return this.recipientId;
  }


}
