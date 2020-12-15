package server.entities;

import java.io.ObjectOutputStream;
import java.net.Socket;

import common.entities.payload.client_to_server.AuthenticatablePayload;

/**
 * Stores an authenticated payload (matching token) from the client 
 * and an output stream that writes to the client socket.
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class AuthenticatedClientRequest extends ClientRequest {

  public AuthenticatedClientRequest(
    AuthenticatablePayload payload, 
    ObjectOutputStream clientOut,
    Socket socket
  ) {
    super(payload, clientOut, socket);
  }

  public AuthenticatablePayload getPayload() {
    return (AuthenticatablePayload)super.getPayload();
  }
}
