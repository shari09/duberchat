package server.entities;

import java.io.ObjectOutputStream;
import java.net.Socket;

import common.entities.payload.Payload;

/**
 * Stores the payload from the client, an output stream that writes
 * to the client socket, and the actual socket itself.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientRequest implements Comparable<ClientRequest> {
  private Payload payload;
  private ObjectOutputStream clientOut;
  private Socket socket;

  public ClientRequest(Payload payload, ObjectOutputStream clientOut, Socket socket) {
    this.payload = payload;
    this.clientOut = clientOut;
    this.socket = socket;
  }

  public int compareTo(ClientRequest other) {
    return this.payload.getPriority() - other.getPayload().getPriority();
  }

  public Payload getPayload() {
    return this.payload;
  }

  public ObjectOutputStream getClientOut() {
    return this.clientOut;
  }

  public Socket getSocket() {
    return this.socket;
  }
}
