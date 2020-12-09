package common.entities.payload;

import common.entities.Message;

/**
 * A payload from server to client that
 * contains a requested amount of messages in a channel before a specific timestamp.
 * before a certain timestamp.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.1
 * @since 1.0.0
 */

public class MessagesToClient extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private Message[] messages;
  private String channelId;

  public MessagesToClient(
    int priority,
    String channelId,
    Message[] messages
  ) {
    super(PayloadType.MESSAGES_TO_CLIENT, priority);
    this.messages = messages;
    this.channelId = channelId;
  }
  
  public Message[] getMessages() {
    return this.messages;
  }

  public String getChannelId() {
    return this.channelId;
  }

}
