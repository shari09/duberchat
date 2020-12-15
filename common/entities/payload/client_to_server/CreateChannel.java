package common.entities.payload.client_to_server;

import java.util.LinkedHashSet;

import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.payload.PayloadType;


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
  private final String name;
  public CreateChannel(
    int priority,
    Token token,
    String userId,
    LinkedHashSet<UserMetadata> participants,
    String name
  ) {
    super(PayloadType.CREATE_CHANNEL, priority, userId, token);
    this.participants = participants;
    this.name = name;
  }

  public LinkedHashSet<UserMetadata> getParticipants() {
    return this.participants;
  }

  public String getName() {
    return this.name;
  }
}
