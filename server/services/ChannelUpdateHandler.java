package server.services;

import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.server_to_client.ClientChannelsUpdate;
import server.entities.EventType;

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
    GlobalServices.serverEventQueue.subscribe(EventType.LEFT_CHANNEL, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    switch (eventType) {
      case CHANNEL_UPDATE:
        this.updateChannels((ChannelMetadata) emitter);
        break;
      case LEFT_CHANNEL:
        this.updateUser((UserMetadata) emitter);
        break;
      default:
        break;
    }
  }

  private void updateChannels(ChannelMetadata channel) {
    for (UserMetadata user: channel.getParticipants()) {
      this.updateUser(user);
    }
  }

  private void updateUser(UserMetadata user) {
    String userId = user.getUserId();
    LinkedHashSet<ChannelMetadata> channels = GlobalServices.users.getChannels(userId);
    
    PayloadService.send(userId, new ClientChannelsUpdate(1, channels));
    GlobalServices.users.save();
  }

}
