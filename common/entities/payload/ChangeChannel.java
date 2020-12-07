package common.entities.payload;

import common.entities.*;

/**
 * A payload from client to server that
 * contains the data for a user to request a change in a channel they administrate.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChangeChannel extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String channelId;
  private final ChannelField fieldToChange;
  private final String newValue;

  public ChangeChannel(
    int priority,
    String userId,
    Token token,
    String channelId,
    ChannelField fieldToChange,
    String newValue
    ) {
    super(
      PayloadType.CHANGE_CHANNEL,
      priority,
      userId,
      token
    );
    
    this.channelId = channelId;
    this.fieldToChange = fieldToChange;
    this.newValue = newValue;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public ChannelField getFieldToChange() {
    return this.fieldToChange;
  }

  public String getNewValue() {
    return this.newValue;
  }

}
