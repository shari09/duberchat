package common.entities.payload.client_to_server;

import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload for the sake of keeping the socket connection alive.
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class KeepAlive extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  public KeepAlive() {
    super(PayloadType.KEEP_ALIVE, 2);
  }

}
