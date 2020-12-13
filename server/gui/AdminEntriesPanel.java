package server.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

import server.entities.EventType;
import server.services.Subscribable;

/**
 * 
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminEntriesPanel extends EntriesPanel implements Subscribable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private JButton broadcast;

  public AdminEntriesPanel() {
    super("Admin");
    this.broadcast = new JButton("Admin");

    // super.addEntry(this.broadcast, content);

    this.activate();
  }

  // public JPanel getBroadcastMsgPanel() {
  //   JPanel panel = new JPanel();
  //   panel.
    
  //   return panel;
  // }

  @Override
  public void activate() {

  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {

  }

  
}
