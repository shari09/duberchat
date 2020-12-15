package server.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;

import server.entities.EventType;
import server.entities.Log;
import server.entities.LogEntrySet;
import server.services.GlobalServices;
import server.services.Subscribable;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogsEntriesPanel extends EntriesPanel implements Subscribable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private LogsPanel curPanel;

  private JButton more;
  private int numLogs;
  private int totalLogs;
  private int loadNumLogs = 5;

  public LogsEntriesPanel() {
    super("Logs");
    this.curPanel = new LogsPanel("Current Session");
    this.numLogs = this.loadNumLogs;
    this.totalLogs = GlobalServices.logging.getNumEntries();
    
    
    this.more = ServerGUIFactory.getIconButton("plus", 30, 10, 10);
    this.more.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LogsEntriesPanel.this.loadMore();
      }
    });
    
    
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    this.getEntriesPanel().add(this.more, c, 0);
    if (this.numLogs >= this.totalLogs) {
      this.more.setVisible(false);
    }

      
    Button current = super.addEntry("Current", this.curPanel);
    this.setFixedDefaultEntry(current);
    this.addLogEntries(GlobalServices.logging.getEntries(1, this.loadNumLogs-1));

    this.activate();
  }

  private void addLogEntries(CopyOnWriteArrayList<LogEntrySet> logEntries) {
    for (int i = 0; i < logEntries.size(); i++) {
      LogEntrySet entrySet = logEntries.get(i);
      String time = entrySet.getLastModified().toString();
      LogsPanel panel = new LogsPanel("Log from "+time);
      for (Log log: entrySet) {
        panel.addLog(log);
      }
      super.addEntry(
        time.substring(0, time.length()-4),
        panel
      );
    }
  }


  private void loadMore() {
    this.addLogEntries(GlobalServices.logging.getEntries(this.numLogs, this.loadNumLogs));
    this.numLogs += this.loadNumLogs;

    if (this.numLogs >= this.totalLogs) {
      this.more.setVisible(false);
    }
  }
  
  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_LOG, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    this.curPanel.addLog((Log)emitter);
  }
  
}
