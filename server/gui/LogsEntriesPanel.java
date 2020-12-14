package server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

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

  public LogsEntriesPanel() {
    super("Logs");
    this.curPanel = new LogsPanel("Current");
    this.numLogs = 5;
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

      
    JButton current = super.addEntry("Current", this.curPanel);
    this.setFixedDefaultEntry(current);
    this.addLogEntries(GlobalServices.logging.getEntries(1, 5));

    this.activate();
  }

  private void addLogEntries(CopyOnWriteArrayList<LogEntrySet> logEntries) {
    for (int i = 0; i < logEntries.size(); i++) {
      LogEntrySet entrySet = logEntries.get(i);
      LogsPanel panel = new LogsPanel(entrySet.getLastModified().toString());
      for (Log log: entrySet) {
        panel.addLog(log);
      }
      super.addEntry(
        entrySet.getLastModified().toString(),
        panel
      );
    }
  }


  private void loadMore() {
    this.addLogEntries(GlobalServices.logging.getEntries(this.numLogs, 5));
    this.numLogs += 5;

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
    this.curPanel.addLog(new Log((String)emitter));
  }
  
}
