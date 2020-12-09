package client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.ClientInfo;
import common.entities.payload.NewUser;
import common.entities.payload.Payload;

/**
 * The client socket for handling socket connection and payloads.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientSocket implements Runnable {
  private Socket socket;
  private InputStream rawInput;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private boolean running;
  private PriorityBlockingQueue<Payload> payloadQueue;
  private ClientMetadata clientMetadata;

  public ClientSocket(String host, int port) throws IOException {
    this.socket = new Socket(host, port);
    this.running = true;
    this.rawInput = this.socket.getInputStream();
    this.input = new ObjectInputStream(this.rawInput);
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.payloadQueue = new PriorityBlockingQueue<Payload>();
  }

  public void run() {
    while (running) {
      synchronized (this.payloadQueue) {
        if (this.payloadQueue.size() > 0) {
          try {
              Payload payloadToSend = this.payloadQueue.poll();
              System.out.println(payloadToSend.toString());
              this.output.writeObject(payloadToSend);
          } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Failed to write payload");
          }
        }
      }
      
      try {
        if (this.rawInput.available() > 0) {
          Payload payload = (Payload)this.input.readObject();
          System.out.println("Response received");
          System.out.println(payload.toString());
          this.processPayload(payload);
          
          // running = false;
        }
      } catch (Exception e) {
        System.out.println("Failed to receive response from server");
        e.printStackTrace();
      }
    }
    
    try {
      this.socket.close();
      this.input.close();
      this.output.close();
    } catch (Exception e) {
      System.out.println("Failed to close sockets");
    }

  }

  public synchronized void processPayload(Payload payload) {
    switch (payload.getType()) {
      case CLIENT_REQUEST_STATUS:
        break;
      case CLIENT_INFO:
        //TODO: finish
        break;
      case MESSAGES_TO_CLIENT:
        //TODO: finish
        break;
      case ATTACHMENT_TO_CLIENT:
        //TODO: finish
        break;
      default:
          System.out.println("Uh oh, an incorrect payload has ended up here");
          break;
    }
  }

  public synchronized void sendPayload(Payload payloadToSend) {
    this.payloadQueue.add(payloadToSend);
  }

}
