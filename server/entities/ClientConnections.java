package server.entities;

import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the mapping of userId to their corresponding output streams
 * and vice versa.
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.1
 */
public class ClientConnections {
  private ConcurrentHashMap<String, ObjectOutputStream> idToSocket;
  private ConcurrentHashMap<ObjectOutputStream, String> clientToId;

  public ClientConnections() {
    this.idToSocket = new ConcurrentHashMap<>();
    this.clientToId = new ConcurrentHashMap<>();
  }

  public void add(String userId, ObjectOutputStream client) {
    this.idToSocket.put(userId, client);
    this.clientToId.put(client, userId);
  }

  public void remove(String userId) {
    ObjectOutputStream client = this.idToSocket.get(userId);
    this.idToSocket.remove(userId);
    this.clientToId.remove(client);
  }

  public void remove(ObjectOutputStream client) {
    String userId = this.clientToId.get(client);
    this.clientToId.remove(client);
    this.idToSocket.remove(userId);
  }

  public boolean hasClient(String userId) {
    return this.idToSocket.contains(userId);
  }

  public ObjectOutputStream getClient(String userId) {
    return this.idToSocket.get(userId);
  }

  public String getUserId(ObjectOutputStream client) {
    return this.clientToId.get(client);
  }
}
