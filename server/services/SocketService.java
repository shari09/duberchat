package server.services;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.entities.Constants;
import common.entities.payload.*;
import server.entities.*;
import server.resources.*;

/**
 * The main server socket for handling client socket connections 
 * and receiving payloads.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.3.0
 * @since 1.0.0
 */

public class SocketService {
  private PayloadProcessor payloadProcessor;
  private ClientQueue clientQueue;
  private ServerSocket server;
  private boolean running = true;
  

  public SocketService() {
    this.payloadProcessor = new PayloadProcessor();
    this.clientQueue = new ClientQueue();
  }

  public void run() {
    Socket client = null;
    try {
      server = new ServerSocket(5000);
      System.out.println("Starting server...");

      while (running) {
        client = server.accept();
        client.setSoTimeout(Constants.SOCKET_TIMEOUT);
        System.out.println("Client accepted: " + client.toString());
        this.clientQueue.add(new ClientHandler(client));
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      e.printStackTrace();
      // this.running = false;
    }
  }

  private class ClientHandler implements Runnable {
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
        GlobalEventQueue.queue.emitEvent(EventType.NEW_CLIENT, 1, client);
        System.out.println("Client connected: " + client.toString());
      } catch (Exception e) {
        System.out.println("Client connection error");
        e.printStackTrace();
      }
    }
  
    public void run() {
      try {
        //accepts payload from clients
        while (this.running) {
          Object obj = this.input.readObject();
          if (!(obj instanceof Payload)) {
            throw new Exception("Unrecognized payload: " + obj);
          }
          Payload payload = (Payload)obj;
          System.out.println("Payload received of type " + payload.getType().toString());
          payloadProcessor.add(payload, this.output);
          GlobalEventQueue.queue.emitEvent(EventType.PAYLOAD, 1, payload);
        }
      } catch (SocketTimeoutException e) { //inactive client timing out
        this.handleDisconnection("has timed out");
      } catch (EOFException e) { //if the client disconnected on their end
        this.handleDisconnection("has disconnected");
      } catch (Exception e) {
        System.out.println("Failed to receive payload from the client");
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
      this.close();
    }

    private void handleDisconnection(String disconnectMsg) {
      String userId = TempData.clientConnections.getUserId(this.output);
      String username = StoredData.users.getUsername(userId);
      System.out.printf(
        "User %s:%s at %s %s\n", 
        userId,
        username,
        this.socket,
        disconnectMsg
      );
      GlobalEventQueue.queue.emitEvent(
        EventType.CLIENT_DISCONNECTED, 
        2, 
        this.output
      );
      this.running = false;
    } 

    public void close() {
      //closing after client is no longer running
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



  private class ClientQueue implements Subscribable {
    private ConcurrentLinkedQueue<ClientHandler> queue;
    private boolean running = false;

    public ClientQueue() {
      this.queue = new ConcurrentLinkedQueue<>();
      GlobalEventQueue.queue.subscribe(EventType.NEW_CLIENT, this);
    }
  
    public void add(ClientHandler client) {
      this.queue.add(client);
    }

    public void onEvent(Object newClient) {
      if (this.running) {
        return;
      }
      this.running = true;
      while (!this.queue.isEmpty()) {
        Thread thread = new Thread(queue.poll());
        thread.start();
      }
      
      this.running = false;
    }
  }
}
