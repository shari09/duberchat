package client.entities;

import common.entities.ClientData;
import common.entities.payload.PayloadType;

/**
 * An interface for objects that listens to the client's data change.
 * <p>
 * Created on 2020.12.11.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ClientSocketListener {

  public void clientDataUpdated(ClientData updatedClientData);

  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  );

}
