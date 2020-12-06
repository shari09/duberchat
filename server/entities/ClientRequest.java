package server.entities;

import java.io.ObjectOutputStream;

import common.entities.payload.ClientToServer;

public class ClientRequest implements Comparable<ClientRequest> {
  private ClientToServer payload;
  private ObjectOutputStream clientOut;

  public ClientRequest(ClientToServer payload, ObjectOutputStream clientOut) {
    this.payload = payload;
    this.clientOut = clientOut;
  }

  public int compareTo(ClientRequest other) {
    return this.payload.getPriority() - other.payload.getPriority();
  }

  public ClientToServer getPayload() {
    return this.payload;
  }

  public ObjectOutputStream getClientOut() {
    return this.clientOut;
  }
}
