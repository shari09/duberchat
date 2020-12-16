package client.entities;

import common.entities.payload.PayloadType;
import common.entities.payload.server_to_client.ServerBroadcast;

/**
 * An interface for objects that listens to a {@code ClientSocket}'s events.
 * <p>
 * Created on 2020.12.11.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ClientSocketListener {

  /**
   * Invoked when the client's data has updated.
   */
  public void clientDataUpdated();

  /**
   * Invoked when the a client request's status is received.
   * @param payloadType   The type of the original request payload.
   * @param successful    Whether or not the request is successful.
   * @param notifMessage  The notification message of the request status.
   */
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  );

  /**
   * Invoked when the server has made a broadcast.
   * @param broadcast The {@code ServerBroadcast} payload received.
   */
  public void serverBroadcastReceived(ServerBroadcast broadcast);

}
