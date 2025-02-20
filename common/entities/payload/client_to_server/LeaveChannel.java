package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server that
 * contains the data for a user to leave a channel.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class LeaveChannel extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String channelId;

  public LeaveChannel(
    int priority,
    String userId,
    Token token,
    String channelId
  ) {
    super(PayloadType.LEAVE_CHANNEL, priority, userId, token);
    this.channelId = channelId;
  }

  public String getChannelId() {
    return this.channelId;
  }

}
