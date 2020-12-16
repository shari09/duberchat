package server.gui;

/**
 * 
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminEntriesPanel extends EntriesPanel {
  /**
   *
   */
  private static final long serialVersionUID = 1L;


  public AdminEntriesPanel() {
    super("Admin");

    BroadcastingPanel broadcast = new BroadcastingPanel();
    DisconnectPanel disconnect = new DisconnectPanel();
    Button broadbastButton = super.addEntry("Broadcast", broadcast);
    super.addEntry("Disconnect", disconnect);

    this.setFixedDefaultEntry(broadbastButton);

  }
  
}
