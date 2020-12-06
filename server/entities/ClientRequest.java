package server.entities;

import java.io.ObjectOutputStream;

import common.entities.payload.*;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientRequest implements Comparable<ClientRequest> {
  private Payload payload;
  private ObjectOutputStream clientOut;

  public ClientRequest(Payload payload, ObjectOutputStream clientOut) {
    this.payload = payload;
    this.clientOut = clientOut;
  }

  public int compareTo(ClientRequest other) {
    return this.payload.getPriority() - other.payload.getPriority();
  }

  public Payload getPayload() {
    return this.payload;
  }

  public ObjectOutputStream getClientOut() {
    return this.clientOut;
  }
}
