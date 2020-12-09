package server.entities;

import java.io.ObjectOutputStream;

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
  public Client(String userId, ObjectOutputStream client) {
    this.userId = userId;
    this.client = client;
  }

  public String getUserId() {
    return this.userId;
  }

  public ObjectOutputStream getClient() {
    return this.client;
  }
  
}
