package server.services;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.entities.payload.ClientToServer;
import common.entities.payload.Payload;

public class SocketService {
  private PayloadProcessor payloadProcessor;
  private ServerSocket server;
  private boolean running = true;
  private final int MAX_THREAD_COUNT = 5000;

  public SocketService() {
    this.payloadProcessor = new PayloadProcessor();
  }

  public void run() {
    Socket client = null;
    try {
      server = new ServerSocket(5000);
      System.out.println("Starting server...");
      ExecutorService pool = Executors.newFixedThreadPool(this.MAX_THREAD_COUNT);

      while (running) {
        client = server.accept();
        System.out.println("Client accepted: " + client.toString());
        pool.execute(new ClientHandler(client));
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      this.running = false;
    }
  }

  class ClientHandler implements Runnable {
    private boolean running = true;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private InputStream rawInput;

    public ClientHandler(Socket client) {
      this.socket = client;
      try {
        this.output = new ObjectOutputStream(client.getOutputStream());
        this.rawInput = client.getInputStream();
        this.input = new ObjectInputStream(client.getInputStream());
        this.running = true;
        System.out.println("Client connected: " + client.toString());
      } catch (Exception e) {
        System.out.println("Client connection error");
        e.printStackTrace();
      }
    }
  
    public void run() {
      while (this.running) {
        try {
          if (this.rawInput.available() > 0) {
            Payload payload = (Payload)this.input.readObject();
            System.out.println("Payload received of type " + payload.getType().toString());
            payloadProcessor.add((ClientToServer)(payload), this.output);
          }
        } catch (Exception e) {
          System.out.println("Failed to receive payload from the client");
          e.printStackTrace();
        }
      }

      //closing after client is no longer running
      try {
        this.input.close();
        this.output.close();
        this.socket.close();
      } catch (Exception e) {
        System.out.println("Failed to close socket");
      }
    }
  }
}
