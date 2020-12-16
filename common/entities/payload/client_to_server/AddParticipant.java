package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server
 * that contains the data for adding a participant to a channel.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class AddParticipant extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String channelId;
  private final String participantId;

  public AddParticipant(
    int priority,
    String userId,
    Token token,
    String channelId,
    String participantId
  ) {
    super(PayloadType.ADD_PARTICIPANT, priority, userId, token);
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
