package server.services;

import java.util.Iterator;
import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.ClientChannelsUpdate;
import server.entities.EventType;
import server.entities.GroupChannel;
import server.resources.GlobalEventQueue;
import server.resources.StoredData;
import server.resources.TempData;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.09.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChannelUpdateHandler implements Subscribable {

  public ChannelUpdateHandler() {

  }

  @Override
  public void activate() {
    GlobalEventQueue.queue.subscribe(EventType.CHANNEL_UPDATE, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    GroupChannel channel = (GroupChannel)emitter;
    Iterator<UserMetadata> itr = channel.getParticipants().iterator();
    while (itr.hasNext()) {
      String userId = itr.next().getUserId();
      LinkedHashSet<ChannelMetadata> channels = 
        StoredData.users.getUser(userId).getChannels();
      PayloadSender.send(
        TempData.clientConnections.getClient(userId),
        new ClientChannelsUpdate(1, channels)
      );
    }
  }
  
}
