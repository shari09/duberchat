package server.services;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.entities.payload.Payload;
import common.entities.payload.PayloadType;
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

      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        "[SUCCESS] Client connected: " + client.toString()
      );
    } catch (Exception e) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        "[ERROR] Client connection"
      );
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
        if (payload.getType() != PayloadType.KEEP_ALIVE) {
          GlobalServices.serverEventQueue.emitEvent(
            EventType.NEW_LOG, 
            1,
            String.format(
              "[SUCCESS] Socket:%s sent %s payload",
              payload.getType().toString(),
              this.socket.toString()
            )
          );
          GlobalServices.serverEventQueue.emitEvent(
            EventType.PAYLOAD, 
            1, 
            new ClientRequest(payload, this.output, this.socket)
          );
        }
        
      }
    } catch (SocketTimeoutException e) { // inactive client timing out
      this.handleDisconnection("has timed out");
    } catch (EOFException e) { // if the client disconnected on their end
      this.handleDisconnection("has disconnected");
    } catch (SocketException e) { // if the client just exited without closing the socket
      this.handleDisconnection(" has reset their connection");
    } catch (Exception e) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "[ERROR] Failed to receive payload from the client\n%s", 
          e.getMessage()
        )
      );
      e.printStackTrace();
    }
    this.close();
  }

  private void handleDisconnection(String disconnectMsg) {
    String userId = GlobalServices.clientConnections.getUserId(this.output);
    if (userId == null) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format("[CONNECTION] %s %s\n", this.socket, disconnectMsg)
      );
      return;
    }
    String username = GlobalServices.users.getUsername(userId);
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CLIENT_DISCONNECTED, 2, this.output
    );
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      0,
      String.format(
        "[CONNECTION] User %s:%s at %s %s\n", 
        username, 
        userId, 
        this.socket, 
        disconnectMsg
      )
    );
    
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
