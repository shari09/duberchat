package server.services;

import java.io.ObjectOutputStream;
import java.util.concurrent.PriorityBlockingQueue;

import common.entities.Token;
import common.entities.payload.AuthenticatablePayload;
import common.entities.payload.ClientInfo;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.Login;
import common.entities.payload.NewUser;
import common.entities.payload.Payload;
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
  private AuthenticatedClientHandler authenticatedUserHandler;
  private AuthenticatedPayloadHandler authenticatedPayloadHandler;
  private boolean running = false;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    this.authenticatedUserHandler = new AuthenticatedClientHandler();
    this.authenticatedPayloadHandler = new AuthenticatedPayloadHandler();
    GlobalEventQueue.queue.subscribe(EventType.PAYLOAD, this);
  }

  public void add(Payload payload, ObjectOutputStream clientOut) {
    ClientRequest c = new ClientRequest(payload, clientOut);
    this.payloadQueue.add(c);
  }

  public void onEvent(Object newPayload) {
    if (this.running) {
      return;
    }
    this.running = true;
    //authenticate clients differently depending on whether or not
    //they are creating a new user, logging in, or sending another request
    while (!PayloadProcessor.this.payloadQueue.isEmpty()) {
      ClientRequest client = PayloadProcessor.this.payloadQueue.poll();
      if (client.getPayload().getType() == PayloadType.LOGIN) {
        authenticateLogin(client);
      } else if (client.getPayload().getType() == PayloadType.NEW_USER) {
        newUser(client);
      } else if (client.getPayload().getType() != PayloadType.KEEP_ALIVE) {
        authenticateToken(client);
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
        new ClientRequestStatus(1, "Unauthenticated token")
      );
      return;
    }

    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, null)
    );
    GlobalEventQueue.queue.emitEvent(
      EventType.AUTHENTICATED_PAYLOAD, 
      1,
      payload
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
      user.getFriends(),
      user.getIncomingFriendRequests(),
      user.getOutgoingFriendRequests(),
      user.getBlocked(),
      user.getChannels()
    );

  }

}
