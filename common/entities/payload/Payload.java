package common.entities.payload;

import java.io.Serializable;

public abstract class Payload implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private PayloadType type;

  public Payload(PayloadType type) {
    this.type = type;
  }

  public PayloadType getType() {
    return this.type;
  }
}
