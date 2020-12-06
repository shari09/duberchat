package common.entities.payload;

import server.entities.*;

/**
 * A payload from client to server that
 * contains the data for a user's download request for a certain file.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GetFile extends AuthenticatablePayload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String fileId;

  public GetFile(
    int priority,
    String userId,
    Token token,
    String fileId
  ) {
    super(
      PayloadType.GET_FILE,
      priority,
      userId,
      token
    );

    this.fileId = fileId;
  }

  public String getFileId() {
    return this.fileId;
  }
}
