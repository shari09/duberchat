package common.entities;

import java.sql.Timestamp;
import java.util.LinkedHashSet;

/**
 * Contains the id and profile information of a private channel.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class PrivateChannelMetadata extends ChannelMetadata {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * 
   * @param channelId
   * @param lastModified
   * @param participants
   */
  public PrivateChannelMetadata(
    String channelId, 
    Timestamp lastModified, 
    LinkedHashSet<UserMetadata> participants
  ) {
    super(channelId, lastModified, participants);
  }

}
