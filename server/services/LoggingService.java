package server.services;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import server.entities.Log;
import server.entities.LogEntrySet;
import server.entities.LogType;

/**
 * Creates a new log set upon server restarting.
 * Old ones are saved in the database and can be loaded upon request.
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 * @see LogsPanel
 */
public class LoggingService {
  private final String LOGS_DIR_PATH = "database/logs/";
  private final String LOG_IDS_PATH = "database/logs/log-entries-ids.ser";
  
  /** Storing all the entry sets' ID in order */
  private CopyOnWriteArrayList<String> logEntriesId; 
  /** Stores current logs */
  private LogEntrySet logEntries;

  /**
   * Initiates a logging service and attempts to load in past log entries.
   * All logs are saved at {@link LoggingService#LOGS_DIR_PATH} with 
   * their log entry ID. A list of all available logs can be found
   * at {@link LoggingService#LOG_IDS_PATH} sorted from latest to earliest.
   */
  public LoggingService() {
    this.logEntries = new LogEntrySet();

    try {
      File logIdsFile = new File(this.LOG_IDS_PATH);
      if (!logIdsFile.exists()) {
        CommunicationService.log("Creating new log save files", LogType.SUCCESS);
        this.logEntriesId = new CopyOnWriteArrayList<>();
        DataService.saveData(this.logEntriesId, this.LOG_IDS_PATH);
      } else {
        this.logEntriesId = DataService.loadData(this.LOG_IDS_PATH);
      }
      this.logEntriesId.add(this.logEntries.getId());
      this.save();

    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Loading log data: %s \n%s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
  }

  /**
   * Saves the log and list of log ID.
   */
  public synchronized void save() {
    try {
      String path = this.LOGS_DIR_PATH + this.logEntries.getId() + ".ser";
      new File(path).getParentFile().mkdirs();
      DataService.saveData(this.logEntries, path);
      DataService.saveData(this.logEntriesId, this.LOG_IDS_PATH);
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "saving log data: %s \n%s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
  }

  /**
   * Add a log to the log entry.
   * @param log    the log
   */
  public void addLog(Log log) {
    this.logEntries.addLog(log);
    this.save();
  }

  /**
   * Load a log entry set from the database given the ID.
   * @param entrySetId   the entry set ID
   * @return             the entry set
   */
  private LogEntrySet getEntrySet(String entrySetId) {
    LogEntrySet entrySet = null;
    try {
      String path = this.LOGS_DIR_PATH + entrySetId + ".ser";
      File entrySetFile = new File(path);
      if (entrySetFile.exists()) {
        entrySet = DataService.loadData(path);
      }

    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Loading log data: %s \n%s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }

    return entrySet;
  }

  /**
   * Get a list of log entries from the database.
   * @param start       starting from the last nth entry
   * @param num         number of entries
   * @return            list of entries
   */
  public CopyOnWriteArrayList<LogEntrySet> getEntries(int start, int num) {
    CopyOnWriteArrayList<LogEntrySet> entries = new CopyOnWriteArrayList<>();
    int idx = this.logEntriesId.size()-start-1;
    for (int i = idx; i >= Math.max(idx-num, 0); i--) {
      LogEntrySet entry = this.getEntrySet(this.logEntriesId.get(i)); 
      if (entry != null) {
        entries.add(entry);
      }
    }

    return entries;
  }

  /**
   * Get all the available log entries in the database.
   * @return         list of all available log files
   */
  public CopyOnWriteArrayList<LogEntrySet> getAllEntries() {
    CopyOnWriteArrayList<LogEntrySet> entries = new CopyOnWriteArrayList<>();
    for (int i = this.logEntriesId.size()-1; i >= 0; i--) {
      entries.add(this.getEntrySet(this.logEntriesId.get(i)));
    }

    return entries;
  }

  /**
   * Get the number of total log entry files in the database.
   * @return       the number of log entry files
   */
  public int getNumEntries() {
    return this.logEntriesId.size();
  }

}
