package server.services;

import java.util.concurrent.PriorityBlockingQueue;

import server.entities.ClientRequest;
import server.entities.EventType;
import server.resources.GlobalEventQueue;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.1
 */
public class AuthenticatedPayloadProcessor implements Subscribable {
  private PriorityBlockingQueue<ClientRequest> payloadQueue;
  private boolean running;
  public AuthenticatedPayloadProcessor() {
    this.payloadQueue = new PriorityBlockingQueue<>();
    this.running = false;
  }

  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.AUTHENTICATED_PAYLOAD, this);
  }

  public void add(ClientRequest payload) {
    this.payloadQueue.add(payload);
  }

  public void onEvent(Object newPayload) {
    if (this.running) {
      return;
    }
    this.running = true;
    while (!this.payloadQueue.isEmpty()) {
      ClientRequest client = this.payloadQueue.poll();
      switch (client.getPayload().getType()) {
        case CHANGE_PASSWORD:
          break;
        case CHANGE_CHANNEL:
          break;
        case CHANGE_PROFILE:
          break;
        case SEND_MESSAGE:
          break;
        case REQUEST_MESSAGES:
          break;
        case FRIEND_REQUEST:
          break;
        case FRIEND_REQUEST_RESPONSE:
          break;
        case GET_FILE:
          break;
        default:
          System.out.println("Uh oh, an incorrect payload has ended up here");
          break;
      }

    }
    this.running = false;
  }
}
