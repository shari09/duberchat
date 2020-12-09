package common.entities.payload;

import java.util.LinkedHashSet;

import common.entities.Token;
import common.entities.UserMetadata;


/**
 * User requesting to create a new group chat.
 * <p>
 * Created on 2020.12.09.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class CreateChannel extends AuthenticatablePayload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final LinkedHashSet<UserMetadata> participants;
  public CreateChannel(
    int priority,
    Token token,
    String userId,
    LinkedHashSet<UserMetadata> participants
  ) {
    super(PayloadType.CREATE_CHANNEL, priority, userId, token);
    this.participants = participants;
  }

  public LinkedHashSet<UserMetadata> getParticipants() {
    return this.participants;
  }
}
