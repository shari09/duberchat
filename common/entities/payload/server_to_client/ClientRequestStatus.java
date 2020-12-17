package common.entities.payload.server_to_client;

import common.entities.payload.Payload;
import common.entities.payload.PayloadType;

/**
 * A payload from server to client that
 * contains the status of a client request
 * and an error message, if applicable.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientRequestStatus extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  
  private final String requestPayloadId;
  private final String errorMessage;

  public ClientRequestStatus(
    int priority,
    String requestPayloadId,
    String errorMessage
  ) {
    super(PayloadType.CLIENT_REQUEST_STATUS, priority);
    this.requestPayloadId = requestPayloadId;
    this.errorMessage = errorMessage;
  }

  public boolean hasError() {
    return this.errorMessage == null;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public String getRequestPayloadId() {
    return this.requestPayloadId;
  }

}
