package server.services;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.entities.payload.Payload;
import server.entities.ClientRequest;
import server.entities.EventType;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.1.0
 * @since 1.0.0
 */
public class ClientHandler implements Runnable {
  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private boolean running;

  public ClientHandler(Socket client) {
    this.socket = client;
    try {
      this.output = new ObjectOutputStream(client.getOutputStream());
      this.input = new ObjectInputStream(client.getInputStream());
      this.running = true;

      System.out.println("Client connected: " + client.toString());
    } catch (Exception e) {
      System.out.println("Client connection error");
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      // accepts payload from clients
      while (this.running) {
        Object obj = this.input.readObject();
        if (!(obj instanceof Payload)) {
          throw new Exception("Unrecognized payload: " + obj);
        }
        Payload payload = (Payload) obj;
        System.out.println("Payload received of type " + payload.getType().toString());
        GlobalServerServices.serverEventQueue.emitEvent(EventType.PAYLOAD, 1, new ClientRequest(payload, this.output));
      }
    } catch (SocketTimeoutException e) { // inactive client timing out
      this.handleDisconnection("has timed out");
    } catch (EOFException e) { // if the client disconnected on their end
      this.handleDisconnection("has disconnected");
    } catch (SocketException e) { // if the client just exited without closing the socket
      this.handleDisconnection(" has reset their connection");
    } catch (Exception e) {
      System.out.println("Failed to receive payload from the client");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    this.close();
  }

  private void handleDisconnection(String disconnectMsg) {
    String userId = GlobalServerServices.clientConnections.getUserId(this.output);
    if (userId == null) {
      System.out.printf("%s %s\n", this.socket, disconnectMsg);
      return;
    }
    String username = GlobalServerServices.users.getUsername(userId);
    System.out.printf("User %s:%s at %s %s\n", userId, username, this.socket, disconnectMsg);
    GlobalServerServices.serverEventQueue.emitEvent(EventType.CLIENT_DISCONNECTED, 2, this.output);
    this.running = false;
  }

  private void close() {
    // closing after client is no longer running
    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (SocketException e) {

    } catch (Exception e) {
      System.out.println("Failed to close socket");
      e.printStackTrace();
    }
  }
}
