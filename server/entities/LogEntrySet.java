package server.entities;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import common.entities.Identifiable;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogEntrySet extends ConcurrentSkipListSet<Log> implements Identifiable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String id;
  private Timestamp lastModified;

  public LogEntrySet() {
    super();
    this.id = UUID.randomUUID().toString();
    this.lastModified = new Timestamp(System.currentTimeMillis());
  }

  @Override
  public String getId() {
    return this.id;
  }

  public Timestamp getLastModified() {
    return this.lastModified;
  }

  public void addLog(Log entry) {
    super.add(entry);
    this.lastModified = new Timestamp(System.currentTimeMillis());
  }
  
}
