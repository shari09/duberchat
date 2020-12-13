package server.gui;

import java.awt.event.ActionEvent;
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

  private ConcurrentHashMap<JButton, JPanel> logEntriesComponents;
  private CopyOnWriteArrayList<LogEntrySet> logEntries;
  private LogsPanel curPanel;


  public LogsEntriesPanel() {
    super("Logs");
    this.logEntriesComponents = new ConcurrentHashMap<>();
    this.logEntries = GlobalServices.logging.getAllEntries();
    this.curPanel = new LogsPanel("Current");
    this.loadLogs();
    super.addEntry(new JButton("Current"), this.curPanel);
    this.activate();
  }

  private void loadLogs() {
    this.logEntries = GlobalServices.logging.getEntries(1, 10);
    for (int i = this.logEntries.size()-1; i >= 0; i--) {
      LogEntrySet entrySet = this.logEntries.get(i);

      LogsPanel panel = new LogsPanel(entrySet.getLastModified().toString());
      for (Log log: entrySet) {
        panel.addLog(log);
      }
      super.addEntry(
        new JButton(entrySet.getLastModified().toString()),
        panel
      );
    }
  }

  



  // @Override
  // public void actionPerformed(ActionEvent e) {
  //   JButton button = (JButton)e.getSource();

  //   GlobalServerServices.guiEventQueue.emitEvent(
  //     EventType.SELECT_LOG, 
  //     2, 
  //     super.getContent(button)
  //   );

  // }

  // public 
  
  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_LOG, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    this.curPanel.addLog(new Log((String)emitter));
  }
  
}
