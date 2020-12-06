package server.services;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.entities.payload.ClientToServer;
import common.entities.payload.Payload;
import server.entities.EventType;
import server.resources.GlobalEventQueue;

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
        System.out.println("Client accepted: " + client.toString());
        this.clientQueue.add(new ClientHandler(client));
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      this.running = false;
    }
  }

  class ClientHandler implements Runnable {
    // private boolean running;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private InputStream rawInput;

    public ClientHandler(Socket client) {
      this.socket = client;
      try {
        this.output = new ObjectOutputStream(client.getOutputStream());
        this.rawInput = client.getInputStream();
        this.input = new ObjectInputStream(this.rawInput);
        GlobalEventQueue.queue.addEvent(EventType.NEW_CLIENT, 1);
        System.out.println("Client connected: " + client.toString());
      } catch (Exception e) {
        System.out.println("Client connection error");
        e.printStackTrace();
      }
    }
  
    public void run() {
        try {
          //accepts payload from clients
          if (this.rawInput.available() > 0) {
            Payload payload = (Payload)this.input.readObject();
            System.out.println("Payload received of type " + payload.getType().toString());
            payloadProcessor.add((ClientToServer)(payload), this.output);
            GlobalEventQueue.queue.addEvent(EventType.PAYLOAD, 1);
          }
        } catch (Exception e) {
          System.out.println("Failed to receive payload from the client");
          System.out.println(e.getMessage());
          e.printStackTrace();
        }
      SocketService.this.clientQueue.add(this);
    }

    public void close() {
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



  class ClientQueue implements Runnable {
    /**
     * Max number of threads listening to client payloads.
     */
    private final int MAX_THREAD_COUNT = 5;
    private ConcurrentLinkedQueue<ClientHandler> queue;
    private ExecutorService pool = Executors.newFixedThreadPool(this.MAX_THREAD_COUNT);
    
    public ClientQueue() {
      this.queue = new ConcurrentLinkedQueue<>();
      GlobalEventQueue.queue.subscribe(EventType.NEW_CLIENT, this);
    }
  
    public void add(ClientHandler client) {
      this.queue.add(client);
    }

    public void run() {
      while (true) {
        while (!this.queue.isEmpty()) {
          pool.execute(queue.poll());
        }   
      }
    }
  }
}
