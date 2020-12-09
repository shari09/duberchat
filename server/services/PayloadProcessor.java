package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Token;
import common.entities.payload.AuthenticatablePayload;
import common.entities.payload.ClientInfo;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.Login;
import common.entities.payload.NewUser;
import common.entities.payload.PayloadType;
import server.entities.Client;
import server.entities.ClientRequest;
import server.entities.EventType;
import server.entities.User;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;
import server.resources.TempData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.1
 * @since 1.0.0
 */

public class PayloadProcessor implements Subscribable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;
  private boolean running;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    this.running = false;
  }

  /**
   * Subscribes to all the events
   */
  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.PAYLOAD, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    ClientRequest clientReq = (ClientRequest)emitter;
    this.payloadQueue.add(clientReq);
    if (this.running) {
      return;
    }
    this.running = true;
    //authenticate clients differently depending on whether or not
    //they are creating a new user, logging in, or sending another request
    while (!this.payloadQueue.isEmpty()) {
      ClientRequest client = this.payloadQueue.poll();
      if (client.getPayload().getType() == PayloadType.LOGIN) {
        this.authenticateLogin(client);
      } else if (client.getPayload().getType() == PayloadType.NEW_USER) {
        this.newUser(client);
      } else if (client.getPayload().getType() != PayloadType.KEEP_ALIVE) {
        this.authenticateToken(client);
      }

    }
    this.running = false;
  }

  /**
   * @param client
   */
  private void authenticateLogin(ClientRequest client) {
    Login payload = (Login) client.getPayload();
    User user = StoredData.users.authenticate(payload.getUsername(), payload.getPassword());
    if (user == null) { // unauthorized
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, "Incorrect username or password")
      );
      return;
    }
    GlobalEventQueue.queue.emitEvent(
      EventType.AUTHENTICATED_CLIENT, 
      1,
      new Client(user.getUserId(), client.getClientOut())
    );
    PayloadSender.send(client.getClientOut(), new ClientRequestStatus(1, null));
    PayloadSender.send(client.getClientOut(), this.getClientInfo(user));
  }

  /**
   * @param client
   */
  private void authenticateToken(ClientRequest client) {
    AuthenticatablePayload payload = (AuthenticatablePayload) client.getPayload();
    boolean authenticated = StoredData.users.authenticateToken(
      payload.getUserId(), 
      payload.getToken()
    );
    if (!authenticated) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, "Unauthorized")
      );
      return;
    }
    GlobalEventQueue.queue.emitEvent(
      EventType.AUTHENTICATED_PAYLOAD, 
      1,
      client
    );
  }

  /**
   * @param client
   */
  private void newUser(ClientRequest client) {
    NewUser payload = (NewUser) client.getPayload();
    User user = StoredData.users.add(
      payload.getUsername(), 
      payload.getPassword(), 
      payload.getDescription()
    );
    if (user == null) { // username taken
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, "Username taken")
      );
      return;
    }
    //request status
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, null)
    );
    //event
    GlobalEventQueue.queue.emitEvent(
      EventType.AUTHENTICATED_CLIENT, 
      1,
      new Client(user.getUserId(), client.getClientOut())
    );
    PayloadSender.send(client.getClientOut(), this.getClientInfo(user));

  }


  private ClientInfo getClientInfo(User user) {
    Token token = TokenService.generateToken();
    TempData.tokens.put(user.getUserId(), token);
    return new ClientInfo(
      1,
      user.getUserId(),
      token,
      user.getStatus(),
      user.getFriends(),
      user.getIncomingFriendRequests(),
      user.getOutgoingFriendRequests(),
      user.getBlocked(),
      user.getChannels()
    );

  }

}
