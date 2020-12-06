package server.services;

import java.io.ObjectOutputStream;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.ClientToServer;
import common.entities.payload.Login;
import common.entities.payload.LoginStatus;
import common.entities.payload.NewUser;
import server.entities.ClientRequest;
import server.entities.Token;
import server.entities.EventType;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;

public class PayloadProcessor implements Runnable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    GlobalEventQueue.queue.subscribe(EventType.PAYLOAD, this);
  }

  public void add(ClientToServer payload, ObjectOutputStream clientOut) {
    ClientRequest c = new ClientRequest(payload, clientOut);
    this.payloadQueue.add(c);
  }

  public void run() {
    while (!PayloadProcessor.this.payloadQueue.isEmpty()) {
      ClientRequest client = PayloadProcessor.this.payloadQueue.poll();
      switch (client.getPayload().getType()) {
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
    Login payload = (Login)client.getPayload();
    Token token = StoredData.users.authenticate(payload.getUsername(), payload.getPassword());
    if (token == null) { //unauthorized
      PayloadSender.send(
        client.getClientOut(), 
        new LoginStatus("Incorrect username or password")
      );
      return;
    }
  }

  /**
   * @param client
   */
  private void newUser(ClientRequest client) {
    NewUser payload = (NewUser)client.getPayload();
    Token token = StoredData.users.add(payload.getUsername(), payload.getPassword());
    if (token == null) { //username taken
      PayloadSender.send(
        client.getClientOut(),
        new LoginStatus("Username taken")
      );
      return;
    }
  }

}
