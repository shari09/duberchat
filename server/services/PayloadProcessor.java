package server.services;

import java.io.ObjectOutputStream;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.ClientToServer;
import common.entities.payload.Login;
import common.entities.payload.NewUser;
import server.entities.ClientRequest;
import server.entities.Token;
import server.services.resources.storeddata.StoredData;

public class PayloadProcessor {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;
  private Thread thread;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    this.thread = new Thread(new Processor());
  }

  public void add(ClientToServer payload, ObjectOutputStream clientOut) {
    this.payloadQueue.add(new ClientRequest(payload, clientOut));
    if (!this.thread.isAlive()) {
      this.thread.start();
    }
    
  }


  class Processor implements Runnable {
    
    public void run() {
      while (!PayloadProcessor.this.payloadQueue.isEmpty()) {
        ClientRequest client = PayloadProcessor.this.payloadQueue.poll();
        switch (client.payload.getType()) {
          case LOGIN:
            authenticateLogin(client);
            break;
          case NEW_USER:
            newUser(client);
            break;
          default:
            break;
        }
        
      }
    }


    /**
     * @param client
     */
    private void authenticateLogin(ClientRequest client) {
      Login payload = (Login)client.payload;
      Token token = StoredData.users.authenticate(payload.getUsername(), payload.getPassword());

    }

    /**
     * @param client
     */
    private void newUser(ClientRequest client) {
      NewUser payload = (NewUser)client.payload;
      if (StoredData.users.usernameTaken(payload.getUsername())) {
        return;
      }

      Token token = StoredData.users.add(payload.getUsername(), payload.getPassword());
      
    }


  }

}
