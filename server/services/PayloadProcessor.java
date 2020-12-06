package server.services;

import java.io.ObjectOutputStream;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.*;
import server.entities.*;
import server.resources.*;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class PayloadProcessor implements Runnable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    GlobalEventQueue.queue.subscribe(EventType.PAYLOAD, this);
  }

  public void add(Payload payload, ObjectOutputStream clientOut) {
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
    Token token = StoredData.users.authenticate(
      payload.getUsername(),
      payload.getPassword()
    );
    if (token == null) { //unauthorized
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, "Incorrect username or password")
      );
      return;
    }
  }

  /**
   * @param client
   */
  private void newUser(ClientRequest client) {
    NewUser payload = (NewUser)client.getPayload();
    Token token = StoredData.users.add(
      payload.getUsername(),
      payload.getPassword(),
      payload.getDescription()
    );
    if (token == null) { //username taken
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(1, "Username taken")
      );
      return;
    }
  }

}
