package server.entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * One log that contains the log message and timestamp.
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
  private LogType type;

  public Log(String log, LogType type) {
    this.created = new Timestamp(System.currentTimeMillis());
    this.log = log;
    this.type = type;
  }

  public Timestamp getCreated() {
    return this.created;
  }

  public String getMsg() {
    return this.log;
  }

  public LogType getType() {
    return this.type;
  }

  @Override
  public int compareTo(Log other) {
    return this.getCreated().compareTo(other.getCreated());
  }


}
