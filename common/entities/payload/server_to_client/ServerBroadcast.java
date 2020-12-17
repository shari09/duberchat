package common.entities.payload.server_to_client;

import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from server to client that
 * contains a broadcast message.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ServerBroadcast extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String message;

  public ServerBroadcast(String message) {
    super(PayloadType.SERVER_BROADCAST, 100); // lol
    this.message = message;

  }

  public String getMessage() {
    return this.message;
  }
}
