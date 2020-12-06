package common.entities.payload;

import server.entities.*;

import java.util.Date;

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
  private final Date timestamp;
  private final int quantity;

  public RequestMessages(
    int priority,
    String userId,
    Token token,
    String channelId,
    Date timestamp,
    int quantity
  ) {
    super(
      PayloadType.REQUEST_MESSAGES,
      priority,
      userId,
      token
    );

    this.channelId = channelId;
    this.timestamp = timestamp;
    this.quantity = quantity;
  }
  
  public String getChannelId() {
    return this.channelId;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public int getQuantity() {
    return this.quantity;
  }

}
