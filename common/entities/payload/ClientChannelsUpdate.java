package common.entities.payload;

import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;

/**
 * An update on the user's channels.
 * <p>
 * Created on 2020.12.09.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClientChannelsUpdate extends Payload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final LinkedHashSet<ChannelMetadata> channels;


  public ClientChannelsUpdate(
    int priority,
    LinkedHashSet<ChannelMetadata> channels
  ) {
    super(PayloadType.CLIENT_CHANNELS_UPDATE, priority);
    this.channels = channels;
  }

  public LinkedHashSet<ChannelMetadata> getChannels() {
    return this.channels;
  }
  
}
