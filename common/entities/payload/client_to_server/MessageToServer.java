package common.entities.payload.client_to_server;

import common.entities.Token;
import common.entities.payload.PayloadType;

/**
 * A payload from client to server that
 * contains the data for a user to send a message in a channel.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class MessageToServer extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  
  private String channelId;
  private String content; // represents filename if has attachment
  private byte[] attachment;
  private String attachmentName;

  public MessageToServer(
    int priority,
    String userId,
    Token token,
    String channelId,
    String content,
    byte[] attachment,
    String attachmentName
  ) {
    super(
      PayloadType.MESSAGE_TO_SERVER,
      priority,
      userId,
      token
    );

    this.channelId = channelId;
    this.content = content;
    this.attachment = attachment;
    this.attachmentName = attachmentName;
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

  public String getAttachmentName() {
    return this.attachmentName;
  }

}
