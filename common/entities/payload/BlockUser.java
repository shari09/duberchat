package common.entities.payload;

import common.entities.Token;

/**
 * Blocking an user.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.2
 */
public class BlockUser extends AuthenticatablePayload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final String blockUsername;

  public BlockUser(
    int priority,
    String userId,
    Token token,
    String blockUsername
  ) {
    super(
      PayloadType.EDIT_MESSAGE,
      priority,
      userId,
      token
    );

    this.blockUsername = blockUsername;
  }

  public String getBlockUsername() {
    return this.blockUsername;
  }

  
}
