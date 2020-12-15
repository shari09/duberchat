package server.services;

import java.util.concurrent.ConcurrentLinkedQueue;

import server.entities.EventType;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.07.
 * 
 * @author Shari Sun
 * @version 1.1.1
 * @since 1.0.0
 */
public class ClientQueue implements Subscribable {
  private ConcurrentLinkedQueue<ClientHandler> queue;

  public ClientQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }

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
