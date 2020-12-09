package common.entities.payload;

import common.entities.Token;

/**
 * Request from user to remove a message.
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemoveMessage extends AuthenticatablePayload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String channelId;
  private String messageId;

  public RemoveMessage(
    int priority,
    String userId,
    Token token,
    String channelId,
    String messageId,
    String newContent
  ) {
    super(
      PayloadType.EDIT_MESSAGE,
      priority,
      userId,
      token
    );

    this.channelId = channelId;
    this.messageId = messageId;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public String getMessageId() {
    return this.messageId;
  }

}
