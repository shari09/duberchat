package server.entities;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Stores the user's userId and its socket's output stream
 * after the user has successfully authenticated themselves either 
 * through logging in or creating a new user.
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Client {
  private String userId;
  private ObjectOutputStream client;
  private Socket socket;
  
  public Client(String userId, ObjectOutputStream client, Socket socket) {
    this.userId = userId;
    this.client = client;
    this.socket = socket;
  }

  public String getUserId() {
    return this.userId;
  }

  public ObjectOutputStream getClient() {
    return this.client;
  }

  public Socket getSocket() {
    return this.socket;
  }
  
}
