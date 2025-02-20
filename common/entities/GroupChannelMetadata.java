package common.entities;

import java.sql.Timestamp;
import java.util.LinkedHashSet;

/**
 * Contains the id and profile information of a group channel.
 * <p>
 * Created on 2020.12.10.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class GroupChannelMetadata extends ChannelMetadata {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  private String channelName;
  private String ownerId;
  
  /**
   * 
   * @param channelId
   * @param lastModified
   * @param participants
   * @param channelName
   */
  public GroupChannelMetadata(
    String channelId, 
    Timestamp lastModified, 
    LinkedHashSet<UserMetadata> participants,
    String channelName,
    String ownerId
  ) {
    super(channelId, lastModified, participants);
    this.channelName = channelName;
    this.ownerId = ownerId;
  }
  
  public String getChannelName() {
    return this.channelName;
  }

  public String getOwnerId() {

    return this.ownerId;
  }

  public void updateOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public void updateChannelName(String name) {
    this.channelName = name;
  }

}
