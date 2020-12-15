package server.services;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import server.entities.Log;
import server.entities.LogEntrySet;

/**
 * Creating log entries that create a new entry set upon server restarts.
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoggingService {
  private final String LOGS_DIR_PATH = "database/logs/";
  private final String LOG_IDS_PATH = "database/logs/log-entries-ids.ser";
  
  /** Storing all the entry sets' ID in order */
  private CopyOnWriteArrayList<String> logEntriesId; 
  /** Stores current logs */
  private LogEntrySet logEntries;
  private int numChanges;
  private final int bufferEntriesNum = 1;

  public LoggingService() {
    this.logEntries = new LogEntrySet();
    this.numChanges = 0;

    try {
      File logIdsFile = new File(this.LOG_IDS_PATH);
      if (!logIdsFile.exists()) {
        System.out.println("Created new save files");
        this.logEntriesId = new CopyOnWriteArrayList<>();
        DataService.saveData(this.logEntriesId, this.LOG_IDS_PATH);
      } else {
        this.logEntriesId = DataService.loadData(this.LOG_IDS_PATH);
      }
      this.logEntriesId.add(this.logEntries.getId());
      this.hardSave();

    } catch (Exception e) {
      System.out.println("Error loading the data");
      e.printStackTrace();
    }
  }

  public void save() {
    this.numChanges++;
    if (this.numChanges >= this.bufferEntriesNum) {
      this.hardSave();
      this.numChanges = 0;
    }
    
  }

  public synchronized void hardSave() {
    try {
      String path = this.LOGS_DIR_PATH + this.logEntries.getId() + ".ser";
      new File(path).getParentFile().mkdirs();
      DataService.saveData(this.logEntries, path);
      DataService.saveData(this.logEntriesId, this.LOG_IDS_PATH);
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public void addLog(String logMsg) {
    this.logEntries.addLog(new Log(logMsg));
    this.save();
  }

  /**
   * 
   * @param entrySetId
   * @return
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
      System.out.println("Error loading the data");
    }

    return entrySet;
  }

  /**
   * 
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

  public CopyOnWriteArrayList<LogEntrySet> getAllEntries() {
    CopyOnWriteArrayList<LogEntrySet> entries = new CopyOnWriteArrayList<>();
    for (int i = this.logEntriesId.size()-1; i >= 0; i--) {
      entries.add(this.getEntrySet(this.logEntriesId.get(i)));
    }

    return entries;
  }

  public int getNumEntries() {
    return this.logEntriesId.size();
  }

}
