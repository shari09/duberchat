package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server that
 * contains the data for a user to transfer the ownership of a channel
 * to another channel participant.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransferOwnership extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
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
