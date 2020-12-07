package common.entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.2
 * @since 1.0.0
 */

public class Token implements Serializable {
  /**
   *
   */
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
