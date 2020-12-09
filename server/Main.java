package server;

import server.services.AuthenticatedClientHandler;
import server.services.AuthenticatedPayloadProcessor;
import server.services.ClientQueue;
import server.services.FriendRequestHandler;
import server.services.MessageQueue;
import server.services.PayloadProcessor;
import server.services.SocketService;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.1.0
 * @since 1.0.0
 */

public class Main {
  public static void main(String[] args) {
    PayloadProcessor payloadProcessor = new PayloadProcessor();
    ClientQueue clientQueue = new ClientQueue();
    AuthenticatedClientHandler authenticatedUserHandler = new AuthenticatedClientHandler();
    AuthenticatedPayloadProcessor authenticatedPayloadProcessor = new AuthenticatedPayloadProcessor();
    MessageQueue messageQueue = new MessageQueue();
    FriendRequestHandler friendReqHandler = new FriendRequestHandler();
    SocketService socket = new SocketService();

    payloadProcessor.activate();
    clientQueue.activate();
    authenticatedPayloadProcessor.activate();
    authenticatedUserHandler.activate();
    messageQueue.activate();
    friendReqHandler.activate();
    socket.run();
  }
}
