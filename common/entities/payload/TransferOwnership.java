package common.entities.payload;

import common.entities.Token;

/**
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransferOwnership extends AuthenticatablePayload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final String channelId;
  private final String recipientId;
  

  public TransferOwnership(
    int priority,
    String userId,
    Token token,
    String channelId,
    String recipientId
  ) {
    super(PayloadType.TRANSFER_OWNERSHIP, priority, userId, token);
    this.channelId = channelId;
    this.recipientId = recipientId;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public String getRecipientId() {
    return this.recipientId;
  }


}
