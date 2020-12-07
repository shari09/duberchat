package common.entities.payload;

/**
 * A payload from server to client that
 * contains a requested file by the user.
 * before a certain timestamp.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class File extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String filename;
  private final byte[] content;

  public File(
    int priority,
    String filename,
    byte[] content
  ) {
    super(PayloadType.FILE, priority);

    this.filename = filename;
    this.content = content;
  }

  public String getFilename() {
    return this.filename;
  }

  public byte[] getContent() {
    return this.content;
  }

}
