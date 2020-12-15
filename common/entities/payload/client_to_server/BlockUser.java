package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * Blocking an user.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
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
      PayloadType.BLOCK_USER,
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
