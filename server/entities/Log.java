package server.entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Log implements Serializable, Comparable<Log> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private Timestamp created;
  private String log;

  public Log(String log) {
    this.created = new Timestamp(System.currentTimeMillis());
    this.log = log;
  }

  public Timestamp getCreated() {
    return this.created;
  }

  public String getMsg() {
    return this.log;
  }

  // @Override
  // public int compareTo(Log other) {
  //   return other.getCreated().compareTo(this.created);
  // }

  @Override
  public int compareTo(Log other) {
    return this.getCreated().compareTo(other.getCreated());
  }


}
