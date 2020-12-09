package common.entities.payload;

import common.entities.Attachment;

/**
 * A payload from server to client that
 * contains a requested file by the user.
 * before a certain timestamp.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class SendAttachment extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final Attachment attachment;

  public SendAttachment(
    int priority,
    Attachment attachment
  ) {
    super(PayloadType.SEND_ATTACHMENT, priority);

    this.attachment = attachment;
  }

  public Attachment getAttachment() {
    return this.attachment;
  }

}
