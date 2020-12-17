package common.entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A token used for client request authentication,
 * received after a user has verified their identity.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Token implements Serializable {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;
  private String value;
  private Timestamp created;

  public Token(String value, Timestamp created) {
    this.value = value;
    this.created = created;
  }

  public String getValue() {
    return this.value;
  }

  public Timestamp getCreatedTime() {
    return this.created;
  }
}
