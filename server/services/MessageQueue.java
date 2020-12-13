package server.services;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.entities.Message;
import common.entities.UserMetadata;
import common.entities.payload.MessageUpdateToClient;
import common.entities.payload.MessageUpdateType;
import server.entities.EventType;

/**
 * A queue that handles message changes and updates connected relevant clients.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageQueue implements Subscribable {
  private ConcurrentLinkedQueue<Message> queue;
  private boolean running;

  public MessageQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
    this.running = false;
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_MESSAGE, this);
    GlobalServices.serverEventQueue.subscribe(EventType.EDIT_MESSAGE, this);
    GlobalServices.serverEventQueue.subscribe(EventType.REMOVE_MESSAGE, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    Message message = (Message) emitter;
    this.queue.add(message);
    // if (this.running) {
    // return;
    // }
    // this.running = true;
    while (!this.queue.isEmpty()) {
      Message msg = this.queue.poll();
      this.sendMessage(msg, eventType);
    }
    // this.running = false;
  }

  private void sendMessage(Message message, EventType eventType) {
    String channelId = message.getChannelId();
    MessageUpdateType type = null;

    switch (eventType) {
      case NEW_MESSAGE:
        type = MessageUpdateType.NEW;
        break;
      case EDIT_MESSAGE:
        type = MessageUpdateType.EDIT;
        break;
      case REMOVE_MESSAGE:
        type = MessageUpdateType.REMOVE;
        break;
      default:
        System.out.println("Wrong event");
        return;
    }

    LinkedHashSet<UserMetadata> users = GlobalServices.channels.getParticipants(channelId);
    Iterator<UserMetadata> itr = users.iterator();
    while (itr.hasNext()) {
      UserMetadata user = itr.next();
      String userId = user.getUserId();
      // TODO: fix the consistency of where to check client existence
      if (GlobalServices.clientConnections.hasClient(userId)) {
        PayloadSender.send(GlobalServices.clientConnections.getClient(userId),
            new MessageUpdateToClient(1, channelId, message, type));
      }
    }
  }
}
