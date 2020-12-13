package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.ClientData;
import common.entities.Token;
import common.entities.payload.AuthenticatablePayload;
import common.entities.payload.ClientInfo;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.Login;
import common.entities.payload.NewUser;
import common.entities.payload.PayloadType;
import server.entities.AuthenticatedClientRequest;
import server.entities.Client;
import server.entities.ClientRequest;
import server.entities.EventType;
import server.entities.User;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class PayloadProcessor implements Subscribable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;
  // private boolean running;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    // this.running = false;
  }

  /**
   * Subscribes to all the events
   */
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.PAYLOAD, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    ClientRequest clientReq = (ClientRequest) emitter;
    this.payloadQueue.add(clientReq);
    // if (this.running) {
    // return;
    // }
    // this.running = true;
    // authenticate clients differently depending on whether or not
    // they are creating a new user, logging in, or sending another request
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
    // this.running = false;
  }

  /**
   * @param client
   */
  private void authenticateLogin(ClientRequest client) {
    Login payload = (Login) client.getPayload();
    User user = GlobalServices.users.authenticate(
      payload.getUsername(), 
      payload.getPassword()
    );
    if (user == null) { // unauthorized
      PayloadSender.send(
        client.getClientOut(),
        new ClientRequestStatus(
          1, payload.getId(), "Incorrect username or password"
        )
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        "Incorrect username or password"
      );
      
      return;
    }
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format("User %s has successfully logged in.", payload.getUsername())
    );
    //event
    GlobalServices.serverEventQueue.emitEvent(
      EventType.AUTHENTICATED_CLIENT, 
      1,
      new Client(user.getId(), client.getClientOut(), client.getSocket())
    );
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
    PayloadSender.send(client.getClientOut(), this.getClientInfo(user));
  }

  /**
   * @param client
   */
  private void authenticateToken(ClientRequest client) {
    AuthenticatablePayload payload = (AuthenticatablePayload) client.getPayload();

    boolean authenticated = GlobalServices.users.authenticateToken(
      payload.getUserId(), 
      payload.getToken()
    );
    if (!authenticated) {
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Unauthorized")
      );
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format("user:%s: unauthorized token", payload.getUserId())
      );
      return;
    }
    GlobalServices.serverEventQueue.emitEvent(
      EventType.AUTHENTICATED_PAYLOAD, 
      1,
      new AuthenticatedClientRequest(
        (AuthenticatablePayload) client.getPayload(), 
        client.getClientOut(), 
        client.getSocket()
      )
    );
  }

  /**
   * @param client
   */
  private void newUser(ClientRequest client) {
    NewUser payload = (NewUser) client.getPayload();
    User user = GlobalServices.users.newUser(
      payload.getUsername(), 
      payload.getPassword(), 
      payload.getDescription()
    );
    if (user == null) { // username taken
      PayloadSender.send(
        client.getClientOut(), 
        new ClientRequestStatus(1, payload.getId(), "Username taken")
      );
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format("Username taken: %s", payload.getUsername())
      );
      return;
    }
    // request status
    PayloadSender.send(
      client.getClientOut(), 
      new ClientRequestStatus(1, payload.getId(), null)
    );
    //log
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      String.format("New user: %s", payload.getUsername())
    );
    // event
    GlobalServices.serverEventQueue.emitEvent(
      EventType.AUTHENTICATED_CLIENT, 
      1,
      new Client(user.getId(), client.getClientOut(), client.getSocket())
    );
    PayloadSender.send(client.getClientOut(), this.getClientInfo(user));

  }

  private ClientInfo getClientInfo(User user) {
    Token token = TokenService.generateToken();
    GlobalServices.tokens.put(user.getId(), token);
    return new ClientInfo(1,
      new ClientData(
        user.getId(), 
        token, 
        user.getUsername(), 
        user.getDescription(), 
        user.getStatus(),
        user.getFriends(), 
        user.getIncomingFriendRequests(), 
        user.getOutgoingFriendRequests(), 
        user.getBlocked(),
        user.getChannels()
      )
    );

  }

}
