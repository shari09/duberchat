package common.entities.payload.server_to_client;

import common.entities.Message;
import common.entities.MessageUpdateType;
import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * Server updating all connected clients who should receive the message.
 * It can be a new message, edit of old message, or removing a message.
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.2
 */
public class MessageUpdateToClient extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private Message message;
  private String channelId;
  private MessageUpdateType updateType;

  public MessageUpdateToClient(
    int priority,
    String channelId,
    Message message,
    MessageUpdateType updateType
  ) {
    super(PayloadType.MESSAGE_UPDATE_TO_CLIENT, priority);
    this.message = message;
    this.channelId = channelId;
    this.updateType = updateType;
  }

  public Message getMessage() {
    return this.message;
  }

  public String getChannelId() {
    return this.channelId;
  }

  public MessageUpdateType getUpdateType() {
    return this.updateType;
  }
  
}
