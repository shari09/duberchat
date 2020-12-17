package common.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;

/**
 * Contains the id and profile information of a channel.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class ChannelMetadata implements Serializable, Comparable<ChannelMetadata> {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private String channelId;
  private Timestamp lastModified;
  private LinkedHashSet<UserMetadata> participants;
  

  public ChannelMetadata(
    String channelId, 
    Timestamp lastModified, 
    LinkedHashSet<UserMetadata> participants
  ) {
    this.channelId = channelId;
    this.lastModified = lastModified;
    this.participants = participants;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public LinkedHashSet<UserMetadata> getParticipants() {
    return this.participants;
  }

  public void updateParticipants(LinkedHashSet<UserMetadata> participants) {
    this.participants = participants;
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

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ChannelMetadata)) {
      return false;
    }
    ChannelMetadata channel = (ChannelMetadata)other;
    return (this.channelId.equals(channel.getChannelId()));
  }

  @Override
  public int hashCode() {
    return this.channelId.hashCode();
  }
}
