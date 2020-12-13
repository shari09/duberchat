package common.entities.payload;

import common.entities.ProfileField;
import common.entities.Token;
import common.entities.UserStatus;

/**
 * A payload from client to server that
 * contains the data for a user to request a change in their profile.
 * <p>
 * Created on 2020.12.10.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UpdateStatus extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final UserStatus status;

  public UpdateStatus(
    int priority,
    String userId,
    Token token,
    UserStatus status
  ) {
    super(
      PayloadType.UPDATE_STATUS,
      priority,
      userId,
      token
    );
    
    this.status = status;
  }

  public UserStatus getStatus() {
    return this.status;
  }

}
