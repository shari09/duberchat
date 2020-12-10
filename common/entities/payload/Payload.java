package common.entities.payload;

import java.io.Serializable;
import java.util.UUID;

import common.entities.Identifiable;

/**
 * Represent the pack of data that is transferred between server and client.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class Payload implements Serializable, Identifiable, Comparable<Payload> {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  private PayloadType type;
  private String id;
  private int priority;

  public Payload(PayloadType type, int priority) {
    this.id = UUID.randomUUID().toString();
    this.type = type;
    this.priority = priority;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int compareTo(Payload otherPayload) {
    return this.priority - otherPayload.getPriority();
  }

  public PayloadType getType() {
    return this.type;
  }

  public int getPriority() {
    return this.priority;
  }

}
