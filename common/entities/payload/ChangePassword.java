package common.entities.payload;

import server.entities.Token;

/**
 * A payload from client to server that
 * contains the data for a user to request a change in their password.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChangePassword extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String originalPassword;
  private final String newPassword;

  public ChangePassword(
    int priority,
    String userId,
    Token token,
    String originalPassword,
    String newPassword
    ) {
    super(
      PayloadType.CHANGE_PASSWORD,
      priority,
      userId,
      token
    );
    
    this.originalPassword = originalPassword;
    this.newPassword = newPassword;
  }

  public String getOriginalPassword() {
    return this.originalPassword;
  }

  public String getNewPassword() {
    return this.newPassword;
  }

}
