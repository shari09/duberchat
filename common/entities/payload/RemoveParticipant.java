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
public class RemoveParticipant extends AuthenticatablePayload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final String channelId;
  private final String participantId;
  

  public RemoveParticipant(
    int priority,
    String userId,
    Token token,
    String channelId,
    String participantId
  ) {
    super(PayloadType.REMOVE_PARTICIPANT, priority, userId, token);
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
