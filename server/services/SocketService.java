package server.services;

import java.net.ServerSocket;
import java.net.Socket;

import common.entities.Constants;
import server.entities.EventType;
import server.entities.LogType;

/**
 * The main server socket for handling client socket connections and receiving
 * payloads.
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun
 * @version 1.3.1
 * @since 1.0.0
 */

public class SocketService implements Runnable {
  private ServerSocket server;
  private boolean running = true;
  private int port;

  public SocketService() {

  }

  /**
   * Starts the socket at a new thread so the main thread can return to its tasks.
   * @param port   the port at which to start the socket at
   */
  public void start(int port) {
    Thread thread = new Thread(this);
    this.port = port;
    thread.start();
  }

  /**
   * Starts the socket that looks out for client connections.
   * Once a client is connected, it will emit a {@code NEW_CLIENT}
   * event and pass the client to the client handler queue.
   * <p>
   * This also sets a timeout on the client socket to disconnect
   * them on inactivity.
   * @see ClientHandler
   * @see Constants#SOCKET_TIMEOUT
   */
  @Override
  public void run() {
    try {
      this.server = new ServerSocket(port);

      while (running) {
        Socket client = server.accept();
        client.setSoTimeout(Constants.SOCKET_TIMEOUT);
        CommunicationService.log("Client accepted: " + client.toString(), LogType.SUCCESS);
        GlobalServices.serverEventQueue.emitEvent(
          EventType.NEW_CLIENT,
          1, 
          new ClientHandler(client)
        );
      }
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Accepting connection: %s \n %s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
  }
}
