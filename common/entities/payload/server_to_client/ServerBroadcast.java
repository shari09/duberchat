package common.entities.payload.server_to_client;

import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServerBroadcast extends Payload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String message;

  public ServerBroadcast(String message) {
    super(PayloadType.SERVER_BROADCAST, 5);
    this.message = message;

  }

  public String getMessage() {
    return this.message;
  }
}
