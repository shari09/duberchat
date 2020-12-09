package server.services;

import java.net.ServerSocket;
import java.net.Socket;

import common.entities.Constants;
import server.entities.EventType;
import server.resources.GlobalEventQueue;

/**
 * The main server socket for handling client socket connections 
 * and receiving payloads.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.3.1
 * @since 1.0.0
 */

public class SocketService {
  private ServerSocket server;
  private boolean running = true;
  

  public SocketService() {

  }

  public void run() {
    try {
      this.server = new ServerSocket(5000);
      System.out.println("Starting server...");

      while (running) {
        Socket client = server.accept();
        client.setSoTimeout(Constants.SOCKET_TIMEOUT);
        System.out.println("Client accepted: " + client.toString());
        GlobalEventQueue.queue.emitEvent(
          EventType.NEW_CLIENT, 
          1, 
          new ClientHandler(client)
        );
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      e.printStackTrace();
      // this.running = false;
    }
  }
}
