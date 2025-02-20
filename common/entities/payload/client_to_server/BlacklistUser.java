package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server
 * that contains the data for blacklisting a user from a channel.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class BlacklistUser extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String channelId;
  private final String participantId;
  
  public BlacklistUser(
    int priority,
    String userId,
    Token token,
    String channelId,
    String participantId
  ) {
    super(PayloadType.BLACKLIST_USER, priority, userId, token);
    this.channelId = channelId;
    this.participantId = participantId;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public String getParticipantId() {
    return this.participantId;
  }

}
