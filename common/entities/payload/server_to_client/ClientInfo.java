package common.entities.payload.server_to_client;

import common.entities.ClientData;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from server to client that
 * contains the {@code ClientData} of a user.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientInfo extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final ClientData clientData;

  public ClientInfo(int priority, ClientData metadata) {
    super(PayloadType.CLIENT_INFO, priority);
    this.clientData = metadata;
  }

  public ClientData getClientData() {
    return this.clientData;
  }
}
