package server.services;

import java.util.concurrent.ConcurrentLinkedQueue;

import server.entities.EventType;

/**
 * A queue of newly connected clients.
 * Each client is given to run on a different thread.
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see SocketService
 * @see ClientHandler
 */
public class ClientQueue implements Subscribable {
  private ConcurrentLinkedQueue<ClientHandler> queue;

  public ClientQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_CLIENT, this);
  }

  @Override
  public void onEvent(Object newClient, EventType eventType) {
    this.queue.add((ClientHandler) newClient);
    while (!this.queue.isEmpty()) {
      Thread thread = new Thread(queue.poll());
      thread.start();
    }
  }
}
