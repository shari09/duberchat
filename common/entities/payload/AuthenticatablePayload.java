package common.entities.payload;

import server.entities.Token;

/**
 * A payload that is authenticatable by user id and token.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class AuthenticatablePayload extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private String userId;
  private Token token;

  public AuthenticatablePayload(
    PayloadType type,
    int priority,
    String userId,
    Token token
  ) {
    super(type, priority);
  }

  public String getUserId() {
    return this.userId;
  }

  public Token getToken() {
    return this.token;
  }

}
