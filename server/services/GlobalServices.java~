package server.services;

import java.util.concurrent.ConcurrentHashMap;

import common.entities.Token;
import server.entities.ClientConnections;

/**
 * A global service handling global events/listener.
 * <p>
 * Created on 2020.12.11.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class GlobalServices {
  public static EventQueueService serverEventQueue = new EventQueueService();
  public static EventQueueService guiEventQueue = new EventQueueService();
  public static UserService users = new UserService();
  public static MessagingService channels = new MessagingService();
  public static ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<>();
  public static ClientConnections clientConnections = new ClientConnections();
  public static LoggingService logging = new LoggingService();
}
