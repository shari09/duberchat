package common.entities.payload;

import common.entities.*;

import java.sql.Timestamp;

/**
 * A payload from client to server that
 * contains the data for a user to request an amount of messages in a channel
 * before a certain timestamp.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class RequestMessages extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  
  private final String channelId;
  private final Timestamp created;
  private final int quantity;

  public RequestMessages(
    int priority,
    String userId,
    Token token,
    String channelId,
    Timestamp created,
    int quantity
  ) {
    super(
      PayloadType.REQUEST_MESSAGES,
      priority,
      userId,
      token
    );

    this.channelId = channelId;
    this.created = created;
    this.quantity = quantity;
  }
  
  public String getChannelId() {
    return this.channelId;
  }

  public Timestamp getCreated() {
    return this.created;
  }

  public int getQuantity() {
    return this.quantity;
  }

}
