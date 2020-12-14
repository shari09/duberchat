package server.entities;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the mapping of userId to their corresponding output streams
 * and vice versa.
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClientConnections {
  private ConcurrentHashMap<String, ObjectOutputStream> idToOutStream;
  private ConcurrentHashMap<ObjectOutputStream, String> clientToId;
  private ConcurrentHashMap<String, Socket> idToSocket;

  public ClientConnections() {
    this.idToOutStream = new ConcurrentHashMap<>();
    this.clientToId = new ConcurrentHashMap<>();
    this.idToSocket = new ConcurrentHashMap<>();
  }

  public void add(String userId, ObjectOutputStream client, Socket socket) {
    this.idToOutStream.put(userId, client);
    this.clientToId.put(client, userId);
    this.idToSocket.put(userId, socket);
  }

  public void remove(String userId) {
    if (!this.idToOutStream.containsKey(userId)) {
      return;
    }
    ObjectOutputStream client = this.idToOutStream.get(userId);
    this.idToOutStream.remove(userId);
    this.clientToId.remove(client);
    this.idToSocket.remove(userId);
  }

  public void remove(ObjectOutputStream client) {
    if (!this.clientToId.containsKey(client)) {
      return;
    }
    String userId = this.clientToId.get(client);
    this.clientToId.remove(client);
    this.idToOutStream.remove(userId);
    this.idToSocket.remove(userId);
  }

  public boolean hasClient(String userId) {
    return this.idToOutStream.containsKey(userId);
  }

  public ObjectOutputStream getClient(String userId) {
    return this.idToOutStream.get(userId);
  }

  public String getUserId(ObjectOutputStream client) {
    return this.clientToId.get(client);
  }

  public Socket getSocket(String userId) {
    return this.idToSocket.get(userId);
  }
}
