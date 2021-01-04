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
import server.entities.LogType;

/**
 * Handles client sockets. This server implements parallel
 * blocking threads where a thread will be blocked while
 * waiting for client input.
 * <p>
 * This waits for a request to come, then throws the payload
 * into the payload processor for it to process.
 * <p>
 * It also checks to see if a client disconnected. If so, it
 * emits a {@code CLIENT_DISCONNECTED} event and subscribers
 * can handle the disconnection of a client.
 * <p>
 * Created on 2020.12.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see PayloadProcessor
 * @see ClientDisconnectHandler
 */
public class ClientHandler implements Runnable {
  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private boolean running;

  /**
   *
   * @param client
   */
  public ClientHandler(Socket client) {
    this.socket = client;
    try {
      this.output = new ObjectOutputStream(client.getOutputStream());
      this.input = new ObjectInputStream(client.getInputStream());
      this.running = true;

      CommunicationService.log("Client connected: " + client.toString(), LogType.SUCCESS);
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Client connection: \n%s", CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
  }

  @Override
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
          CommunicationService.log(String.format(
            "%s sent %s payload",
            this.socket.toString(),
            payload.getType().toString()
          ), LogType.SUCCESS);
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
      CommunicationService.log(String.format(
        "Failed to receive payload from the client\n%s\n%s",
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
    this.close();
  }

  /**
   * Sends the appropriate logs and events once a client disconnected.
   */
  private void handleDisconnection(String disconnectMsg) {
    String userId = GlobalServices.clientConnections.getUserId(this.output);
    if (userId == null) {
      CommunicationService.log(String.format(
        "%s %s\n", this.socket, disconnectMsg
      ), LogType.CONNECTION);
      return;
    }
    String username = GlobalServices.users.getUsername(userId);
    GlobalServices.serverEventQueue.emitEvent(
      EventType.CLIENT_DISCONNECTED, 2, this.output
    );
    CommunicationService.log(String.format(
      "User %s:%s at %s %s\n",
      username,
      userId,
      this.socket,
      disconnectMsg
    ), LogType.CONNECTION);

    this.running = false;
  }

  /**
   * close the sockets after the client disconnected.
   */
  private void close() {
    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (SocketException e) {

    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Failed to close socket: %s \n%s",
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
  }
}
