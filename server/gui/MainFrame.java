package server.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import common.gui.Theme;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.Subscribable;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.11.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainFrame extends JFrame implements Subscribable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final int WIDTH = 1000;
  private static final int HEIGHT = 600;

  private SidePanel sidePanel;
  private LogsEntriesPanel logsPanel;
  private AdminEntriesPanel adminPanel;
  private EntriesPanel curEntriesPanel;

  private UsersEntriesPanel usersPanel;

  private JPanel entryContentPanel;
  private JPanel nullPanel;

  private GridBagConstraints c;

  public MainFrame() {
    super(Theme.APPLICATION_NAME + " Server");
    this.setIconImage(Theme.getIcon());
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
    this.setResizable(false);
    this.setBackground(ServerGUIFactory.START_BACKGROUND);

    this.setLayout(new GridBagLayout());
    this.c = new GridBagConstraints();
    this.sidePanel = new SidePanel();
    this.usersPanel = new UsersEntriesPanel();
    this.logsPanel = new LogsEntriesPanel();
    this.adminPanel = new AdminEntriesPanel();
    
    this.curEntriesPanel = this.logsPanel;

    this.nullPanel = new JPanel();
    


    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0;
    c.weighty = 1;
    this.getContentPane().add(this.sidePanel, c);
    c.gridx = 1;
    c.weightx = 0;

    this.getContentPane().add(this.usersPanel, c);
    this.getContentPane().add(this.logsPanel, c);
    this.getContentPane().add(this.adminPanel, c);
    

    this.logsPanel.setVisible(true);
    c.gridx = 2;
    c.weightx = 1;
    this.entryContentPanel = this.logsPanel.getDefaultContent();
    this.getContentPane().add(this.entryContentPanel, c);
    this.setVisible(true);
  }

  @Override
  public void activate() {
    GlobalServices.guiEventQueue.subscribe(EventType.LOGS_TAB, this);
    GlobalServices.guiEventQueue.subscribe(EventType.USERS_TAB, this);
    GlobalServices.guiEventQueue.subscribe(EventType.ADMIN_TAB, this);
    GlobalServices.guiEventQueue.subscribe(EventType.ENTRY_SELECTED, this);
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_LOG, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    switch(eventType) {
      case LOGS_TAB:
        this.switchEntriesPanel(this.logsPanel);
        this.switchContentPanel(this.logsPanel.getDefaultContent());
        break;
      case USERS_TAB:
        this.switchEntriesPanel(this.usersPanel);
        this.switchContentPanel(this.usersPanel.getDefaultContent());
        break;
      case ADMIN_TAB:
        this.switchEntriesPanel(this.adminPanel);
        this.switchContentPanel(this.adminPanel.getDefaultContent());
        break;
      case ENTRY_SELECTED:
        this.switchContentPanel(this.curEntriesPanel.getContent((JButton)emitter));
        break;
      case NEW_LOG:
        this.curEntriesPanel.requestFocus();
        this.setVisible(true);
        break;
      default:
        break;
    }
  }

  /**
   * Switch panels - remove old panel and add new panel.
   * @param panel the panel to switch to.
   */
  private void switchEntriesPanel(EntriesPanel panel) {
    this.curEntriesPanel.setVisible(false);
    this.curEntriesPanel = panel;
    this.curEntriesPanel.setVisible(true);
    this.curEntriesPanel.requestFocus();
    this.setVisible(true);
  }

  /**
   * Switch panels - remove old panel and add new panel.
   * @param panel the panel to switch to.
   */
  private void switchContentPanel(JPanel panel) {
    if (panel == null) {
      panel = this.nullPanel;
    }
    this.entryContentPanel.setVisible(false);
    this.entryContentPanel = panel;
    this.entryContentPanel.setVisible(true);
    this.getContentPane().add(this.entryContentPanel, this.c);
    this.entryContentPanel.requestFocus();
    this.setVisible(true);
  }

  

  

  
}
