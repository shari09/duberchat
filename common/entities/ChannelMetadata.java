package common.entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Contains the id and profile information of a channel.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */


public class ChannelMetadata implements Serializable, Comparable<ChannelMetadata> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String channelId;
  private String channelName;
  private Timestamp lastModified;

  public ChannelMetadata(String channelId, String channelName, Timestamp lastModified) {
    this.channelId = channelId;
    this.channelName = channelName;
    this.lastModified = lastModified;
  }


  public String getChannelId() {
    return this.channelId;
  }

  public String getChannelName() {
    return this.channelName;
  }

  public void updateChannelName(String name) {
    this.channelName = name;
  }

  public int compareTo(ChannelMetadata other) {
    return other.getLastModified().compareTo(this.getLastModified());
  }

  public Timestamp getLastModified() {
    return this.lastModified;
  }

 public void updateLastModified(Timestamp now) {
    this.lastModified = now;
  }
}
