package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.ClientData;
import common.entities.Token;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.AuthenticatablePayload;
import common.entities.payload.client_to_server.Login;
import common.entities.payload.client_to_server.NewUser;
import common.entities.payload.server_to_client.ClientInfo;
import server.entities.AuthenticatedClientRequest;
import server.entities.Client;
import server.entities.ClientRequest;
import server.entities.EventType;
import server.entities.User;

/**
 * All the client payload comes here first.
 * This is subscribed to {@code PAYLOAD} event.
 * <p>
 * This authenticate clients differently depending on whether or not
 * they are creating a new user, logging in, or sending another request
 * that needs to be authenticated (token).
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see Token
 * @see AuthenticatedPayloadProcessor
 */

public class PayloadProcessor implements Subscribable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;

  public PayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.PAYLOAD, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    ClientRequest clientReq = (ClientRequest) emitter;
    this.payloadQueue.add(clientReq);
    
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
   * Authenticates a returning user for logging in 
   * and sends corresponding responses/events based on whether the user
   * successfully logs in or not.
   * <p>
   * Then, this will send the client data about their profile.
   * <p>
   * This also emits an {@code AUTHENTICATED_CLIENT} event.
   * @param client   the client request data
   * @see            AuthenticatedClientHandler
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

    
    CommunicationService.sendResponse(
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
      CommunicationService.send(client.getClientOut(), this.getClientInfo(user));
    }
    
  }

  /**
   * Authentiate the client's token to make sure they are authorized.
   * If they are, the payload will be thrown into {@link AuthenticatedPayloadProcessor}
   * and continue from there. If not, it will stop here and do nothing.
   * @param client     the client request data
   */
  private void authenticateToken(ClientRequest client) {
    AuthenticatablePayload payload = (AuthenticatablePayload) client.getPayload();

    boolean authenticated = GlobalServices.users.authenticateToken(
      payload.getUserId(), 
      payload.getToken()
    );

    CommunicationService.sendResponse(
      client, 
      authenticated, 
      String.format("Authenticating request from user:%s", payload.getUserId()), 
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
   * Creates a new user and send responses depending the status
   * of this request.
   * <p>
   * Then, this will send the client data about their profile,
   * which is just the information they put when registering 
   * an account.
   * <p>
   * This also emits an {@code AUTHENTICATED_CLIENT} event.
   * @param client     the client request data
   * @see              AuthenticatedClientHandler
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

    CommunicationService.sendResponse(
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
      CommunicationService.send(client.getClientOut(), this.getClientInfo(user));
    }   

  }

  /**
   * When a new/returning user logs in, the server updates them
   * with their profile data.
   * @param user  the user
   * @return      the ClientInfo
   * @see         ClientInfo
   * @see         ClientData
   */
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
