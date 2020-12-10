package client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.ClientInfo;
import common.entities.payload.ClientRequestStatus;
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
  private PriorityBlockingQueue<Payload> payloadQueue;
  private PriorityBlockingQueue<ErrorMessage> errorMessages;
  private ConcurrentHashMap<String, Payload> pendingRequests;

  public ClientSocket(String host, int port) throws IOException {
    this.socket = new Socket(host, port);    
    this.rawInput = this.socket.getInputStream();
    this.input = new ObjectInputStream(this.rawInput);
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.payloadQueue = new PriorityBlockingQueue<Payload>();
    this.errorMessages = new PriorityBlockingQueue<ErrorMessage>();
    this.pendingRequests = new ConcurrentHashMap<String, Payload>();
  }

  public void run() {
    while (true) {
      synchronized (this.payloadQueue) {
        if (this.payloadQueue.size() > 0) {
          try {
              Payload payloadToSend = this.payloadQueue.poll();
              System.out.println(payloadToSend.toString());
              this.output.writeObject(payloadToSend);
              this.pendingRequests.put(payloadToSend.getId(), payloadToSend);
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

  }

  public synchronized void processPayload(Payload payload) {
    switch (payload.getType()) {
      case CLIENT_REQUEST_STATUS:
        this.processRequestStatus((ClientRequestStatus)payload);
        break;

      case CLIENT_INFO:
        GlobalClient.clientsInfo.put(
          this.socket,
          ((ClientInfo)payload).getClientData()
        );
        break;

      case MESSAGES_TO_CLIENT:
        //TODO: finish
        break;
      case ATTACHMENT_TO_CLIENT:
        //TODO: finish
        break;
      default:
        System.out.println("unknown payload type");
        break;
    }
  }

  public synchronized void processRequestStatus(ClientRequestStatus requestStatus) {
    Payload originalPayload = this.pendingRequests.get(requestStatus.getRequestPayloadId());
    String errorMessage = requestStatus.getErrorMessage();

    if (originalPayload == null) {
      System.out.println("original client request not found");
      return;
    }

    if (errorMessage != null) {
      this.errorMessages.add(new ErrorMessage(requestStatus.getPriority(), errorMessage));
      System.out.println("An error has occurred! (" + errorMessage + ")");
      return;
    }

    // error message is null: request success
    switch (originalPayload.getType()) {
      case NEW_USER:
        break;
      case LOGIN:
        break;
      case CHANGE_PASSWORD:
        break;
      //case :
      //  break;
      default:
        System.out.println("unknown payload type");
        break;
    }
  }

  public synchronized void close() throws IOException {
    this.socket.close();
    this.input.close();
    this.output.close();
  }

  public synchronized void sendPayload(Payload payloadToSend) {
    this.payloadQueue.add(payloadToSend);
  }

  public Socket getSocket() {
    return this.socket;
  }

  public PriorityBlockingQueue<ErrorMessage> getErrorMessages() {
    return this.errorMessages;
  }

}
