package common.entities.payload;

import java.io.Serializable;

/**
 * Represent the pack of data that is transferred between server and client.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class Payload implements Serializable {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  private PayloadType type;
  private int priority;

  public Payload(PayloadType type, int priority) {
    this.type = type;
    this.priority = priority;
  }

  public PayloadType getType() {
    return this.type;
  }

  public int getPriority() {
    return this.priority;
  }

}
