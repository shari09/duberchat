package common.entities.payload;

import server.entities.*;

/**
 * A payload from server to client that
 * contains a requested amount of messages in a channel before a specific timestamp.
 * before a certain timestamp.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Messages extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final Message[] messages;

  public Messages(
    int priority,
    String channelId,
    Message[] messages
  ) {
    super(PayloadType.MESSAGES, priority);

    this.messages = messages;
  }
  
  public Message[] getMessages() {
    return this.messages;
  }

}
