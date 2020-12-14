package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.ClientData;
import common.entities.Token;
import common.entities.payload.AuthenticatablePayload;
import common.entities.payload.ClientInfo;
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

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
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

    boolean success = false;
    String errorMsg = "";

    if (user == null) {
      errorMsg = "Incorrect username or password";
    } else if (GlobalServices.clientConnections.hasClient(user.getId())) {
      errorMsg = "Please log out of your other window first";
    } else {
      success = true;
    }

    
    PayloadService.sendResponse(
      client, 
      success, 
      String.format("Logging in username:%s", payload.getUsername()), 
      errorMsg
    );
    
    
    if (success) {
      //event
      GlobalServices.serverEventQueue.emitEvent(
        EventType.AUTHENTICATED_CLIENT, 
        1,
        new Client(user.getId(), client.getClientOut(), client.getSocket())
      );
      PayloadService.send(client.getClientOut(), this.getClientInfo(user));
    }
    
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

    PayloadService.sendResponse(
      client, 
      authenticated, 
      String.format("Authenticating user:%s", payload.getUserId()), 
      "Unauthorized"
    );

    if (authenticated) {
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

    boolean success = false;
    if (user != null) {
      success = true;
    }

    PayloadService.sendResponse(
      client, 
      success, 
      String.format("New user with username:%s", payload.getUsername()), 
      "Username taken"
    );

    // event
    if (success) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.AUTHENTICATED_CLIENT, 
        1,
        new Client(user.getId(), client.getClientOut(), client.getSocket())
      );
      PayloadService.send(client.getClientOut(), this.getClientInfo(user));
    }   

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
