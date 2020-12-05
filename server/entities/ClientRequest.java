package server.entities;

import java.io.ObjectOutputStream;

import common.entities.payload.ClientToServer;

public class ClientRequest implements Comparable<ClientRequest> {
  public ClientToServer payload;
  public ObjectOutputStream toClient;

  public ClientRequest(ClientToServer payload, ObjectOutputStream client) {
    this.payload = payload;
    this.toClient = client;
  }

  public int compareTo(ClientRequest other) {
    return this.payload.getPriority() - other.payload.getPriority();
  }
}
