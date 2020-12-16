package server.services;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.entities.Message;
import common.entities.MessageUpdateType;
import common.entities.UserMetadata;
import common.entities.payload.server_to_client.MessageUpdateToClient;
import server.entities.EventType;
import server.entities.LogType;

/**
 * A queue that listens for new/edit/remove of messages.
 * It sends the message to all the connected clients in the channel.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageQueue implements Subscribable {
  private ConcurrentLinkedQueue<Message> queue;

  public MessageQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
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
    while (!this.queue.isEmpty()) {
      Message msg = this.queue.poll();
      this.sendMessage(msg, eventType);
    }
  }

  /**
   * Iterates through all the participants in the channel
   * and send corresponding payloads to each participant.
   * @param message        the updated message
   * @param eventType      the message update type
   * @see                  MessageUpdateType
   */
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
        CommunicationService.log(String.format(
          "Wrong event: %s", 
          eventType 
        ), LogType.CLIENT_ERROR);
        return;
    }

    LinkedHashSet<UserMetadata> users = GlobalServices.channels.getParticipants(channelId);
    Iterator<UserMetadata> itr = users.iterator();
    while (itr.hasNext()) {
      UserMetadata user = itr.next();
      String userId = user.getUserId();
      if (GlobalServices.clientConnections.hasClient(userId)) {
        CommunicationService.send(
          GlobalServices.clientConnections.getClient(userId),
          new MessageUpdateToClient(1, channelId, message, type)
        );
      }
    }
  }
}
