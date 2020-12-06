package common.entities.payload;

/**
 * A payload from server to client that
 * contains the status of a client request
 * as well as an error message, if applicable.
 * <p>
 * Created on 2020.12.06.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientRequestStatus extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String errorMessage;

  public ClientRequestStatus(
    int priority,
    String errorMessage
  ) {
    super(PayloadType.CLIENT_REQUEST_STATUS, priority);
    this.errorMessage = errorMessage;
  }

  public boolean hasError() {
    return this.errorMessage == null;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

}
