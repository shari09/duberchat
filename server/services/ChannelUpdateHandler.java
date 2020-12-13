package server.services;

import java.util.Iterator;
import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.ClientChannelsUpdate;
import common.entities.payload.PayloadType;
import server.entities.EventType;
import server.entities.GroupChannel;

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
    GlobalServices.serverEventQueue.subscribe(EventType.CHANNEL_UPDATE, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    ChannelMetadata channel = (ChannelMetadata) emitter;

    for (UserMetadata user: channel.getParticipants()) {
      String userId = user.getUserId();
      LinkedHashSet<ChannelMetadata> channels = GlobalServices.users.getChannels(userId);
      PayloadSender.send(userId, new ClientChannelsUpdate(1, channels));
      //log
      GlobalServices.serverEventQueue.emitEvent(
        EventType.NEW_LOG, 
        1,
        String.format(
          "sent %s payload to user:%s", 
          PayloadType.CLIENT_CHANNELS_UPDATE,
          userId
        )
      );
    }
  }

}
