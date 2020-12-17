package common.entities.payload.server_to_client;

import common.entities.Message;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from server to client that
 * contains a requested amount of messages in a channel before a specific timestamp.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class MessagesToClient extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String channelId;
  private final Message[] messages;

  public MessagesToClient(
    int priority,
    String channelId,
    Message[] messages
  ) {
    super(PayloadType.MESSAGES_TO_CLIENT, priority);
    this.channelId = channelId;
    this.messages = messages;
  }
  
  public String getChannelId() {
    return this.channelId;
  }

  public Message[] getMessages() {
    return this.messages;
  }

}