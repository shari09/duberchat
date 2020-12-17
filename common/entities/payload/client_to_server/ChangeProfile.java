package common.entities.payload.client_to_server;

import common.entities.ProfileField;
import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server that
 * contains the data for a user to request a change in their profile.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChangeProfile extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final ProfileField fieldToChange;
  private final String newValue;

  public ChangeProfile(
    int priority,
    String userId,
    Token token,
    ProfileField fieldToChange,
    String newValue
  ) {
    super(
      PayloadType.CHANGE_PROFILE,
      priority,
      userId,
      token
    );
    
    this.fieldToChange = fieldToChange;
    this.newValue = newValue;
  }

  public ProfileField getFieldToChange() {
    return this.fieldToChange;
  }

  public String getNewValue() {
    return this.newValue;
  }

}
