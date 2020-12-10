package common.entities.payload;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;

/**
 * A payload from server to client that
 * contains the metadata of a user.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientInfo extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private ClientData clientData;

  public ClientInfo(int priority, ClientData metadata) {
    super(PayloadType.CLIENT_INFO, priority);
    this.clientData = metadata;
  }

  public ClientData getClientData() {
    return this.clientData;
  }
}
