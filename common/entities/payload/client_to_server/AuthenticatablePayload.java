package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server
 * that is authenticatable by user id and token.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class AuthenticatablePayload extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String userId;
  private final Token token;

  public AuthenticatablePayload(
    PayloadType type,
    int priority,
    String userId,
    Token token
  ) {
    super(type, priority);
    this.userId = userId;
    this.token = token;
  }

  public String getUserId() {
    return this.userId;
  }

  public Token getToken() {
    return this.token;
  }

}
