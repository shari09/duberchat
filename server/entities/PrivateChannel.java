package server.entities;

import java.util.LinkedHashSet;

import common.entities.ChannelMetadata;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;

/**
 * A private channel between two people.
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class PrivateChannel extends Channel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public PrivateChannel(UserMetadata userOne, UserMetadata userTwo) {
    super(new LinkedHashSet<UserMetadata>());
    super.addParticipant(userOne);
    super.addParticipant(userTwo);
  }

  public boolean addParticipant(UserMetadata user) {
    return false;
  }

  public boolean removeParticipant(UserMetadata user) {
    return false;
  }
  public ChannelMetadata getNewMetadata() {
    return new PrivateChannelMetadata(
      this.getId(), 
      this.getLastModified(),
      this.getParticipants()
    );
  }
}
