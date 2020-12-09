package common.entities;

import java.io.Serializable;

/**
 * Contains the id and profile information of a channel.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

//TODO: finish
public class ChannelMetadata implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String channelId;
  private String channelName;

  public ChannelMetadata(String channelId, String channelName) {
    this.channelId = channelId;
    this.channelName = channelName;
  }


  public String getChannelId() {
    return this.channelId;
  }

  public String getChannelName() {
    return this.channelName;
  }

}
