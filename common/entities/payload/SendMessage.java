package common.entities.payload;

import server.entities.*;

/**
 * A payload from client to server that
 * contains the data for a user to send a message in a channel.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class SendMessage extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  
  private final String channelId;
  private final String content; // represents filename if has attachment
  private final byte[] attachment;

  public SendMessage(
    int priority,
    String userId,
    Token token,
    String channelId,
    String content,
    byte[] attachment
  ) {
    super(
      PayloadType.SEND_MESSAGE,
      priority,
      userId,
      token
    );

    this.channelId = channelId;
    this.content = content;
    this.attachment = attachment;
  }

  public boolean hasAttachment() {
    return this.attachment != null;
  }
  
  public String getChannelId() {
    return this.channelId;
  }

  public String getContent() {
    return this.content;
  }

  public byte[] getAttachment() {
    return this.attachment;
  }

}
