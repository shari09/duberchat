package common.entities.payload;

import common.entities.Token;

/**
 * A payload from client to server that
 * contains the data for a user's download request for a certain file.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class RequestAttachment extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String attachmentId;

  public RequestAttachment(
    int priority,
    String userId,
    Token token,
    String attachmentId
  ) {
    super(
      PayloadType.REQUEST_ATTACHMENT,
      priority,
      userId,
      token
    );

    this.attachmentId = attachmentId;
  }

  public String getAttachmentId() {
    return this.attachmentId;
  }
}
