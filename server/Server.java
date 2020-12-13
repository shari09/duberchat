package server;

import server.gui.MainFrame;
import server.gui.StartFrame;
import server.services.AuthenticatedClientHandler;
import server.services.AuthenticatedPayloadProcessor;
import server.services.ClientQueue;
import server.services.FriendInfoUpdater;
import server.services.LoggingHandler;
import server.services.MessageQueue;
import server.services.ChannelUpdateHandler;
import server.services.PayloadProcessor;
import server.services.SocketService;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Server {
  public Server() {

  }

  public void start() {
    PayloadProcessor payloadProcessor = new PayloadProcessor();
    ClientQueue clientQueue = new ClientQueue();
    AuthenticatedClientHandler authenticatedUserHandler = new AuthenticatedClientHandler();
    AuthenticatedPayloadProcessor authenticatedPayloadProcessor = new AuthenticatedPayloadProcessor();
    MessageQueue messageQueue = new MessageQueue();
    FriendInfoUpdater friendInfoUpdater = new FriendInfoUpdater();
    ChannelUpdateHandler channelUpdateHandler = new ChannelUpdateHandler();
    SocketService socket = new SocketService();
    LoggingHandler log = new LoggingHandler();


    payloadProcessor.activate();
    clientQueue.activate();
    authenticatedPayloadProcessor.activate();
    authenticatedUserHandler.activate();
    messageQueue.activate();
    friendInfoUpdater.activate();
    channelUpdateHandler.activate();
    log.activate();
    socket.start();
    (new MainFrame()).activate();; 
  }

  public static void main(String[] args) {

    Server server = new Server();
    
    StartFrame start = new StartFrame(server);
  }
}
