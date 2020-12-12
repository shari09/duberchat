package server.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import server.entities.Client;
import server.entities.EventType;
import server.services.GlobalServerServices;
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
  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;

  private SidePanel sidePanel;
  private EntriesPanel usersPanel;
  private EntriesPanel logsPanel;
  private EntriesPanel curEntriesPanel;

  private ConcurrentHashMap<String, JButton> users;

  public MainFrame() {
    super("DuberChat Server");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
    this.setResizable(false);
    this.setBackground(Color.BLACK);

    this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    this.sidePanel = new SidePanel();
    this.sidePanel.setPreferredSize(new Dimension(
      sidePanel.getPreferredSize().width, 
      MainFrame.HEIGHT
    ));

    this.usersPanel = new EntriesPanel("Connected Users");
    this.usersPanel.setPreferredSize(new Dimension(
      this.usersPanel.getPreferredSize().width, 
      MainFrame.HEIGHT
    ));
    this.logsPanel = new EntriesPanel("Logs");
    this.logsPanel.setPreferredSize(new Dimension(
      this.logsPanel.getPreferredSize().width, 
      MainFrame.HEIGHT
    ));

    this.users = new ConcurrentHashMap<>();

    
    this.curEntriesPanel = this.usersPanel;


    this.getContentPane().add(this.sidePanel);
    this.getContentPane().add(this.curEntriesPanel);
    this.setVisible(true);
    this.usersPanel.addEntry(new JButton("dsjhf"), new JPanel());
    this.usersPanel.addEntry(new JButton("dsjhf"), new JPanel());
    this.usersPanel.addEntry(new JButton("dsjhf"), new JPanel());
    this.usersPanel.addEntry(new JButton("dsjhf"), new JPanel());
  }

  @Override
  public void activate() {
    GlobalServerServices.guiEventQueue.subscribe(EventType.LOGS_TAB, this);
    GlobalServerServices.guiEventQueue.subscribe(EventType.USERS_TAB, this);
    GlobalServerServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
    GlobalServerServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    switch(eventType) {
      case LOGS_TAB:
        this.switchEntriesPanel(this.logsPanel);
        break;
      case USERS_TAB:
        this.switchEntriesPanel(this.usersPanel);
        break;
      case AUTHENTICATED_CLIENT:
        this.newClient((Client)emitter);
        break;
      case CLIENT_DISCONNECTED:
        this.removeClient(emitter);
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
    this.getContentPane().add(this.curEntriesPanel);
    this.curEntriesPanel.requestFocus();
    this.setVisible(true);
  }

  private void newClient(Client client) {
    String username = GlobalServerServices.users.getUsername(client.getUserId());
    JButton user = new JButton(username);
    this.users.put(client.getUserId(), user);
    this.usersPanel.addEntry(user, new JPanel());
    System.out.println("SKDFJ");
  }

  private void removeClient(Object emitter) {
    String userId;
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      userId = GlobalServerServices.clientConnections.getUserId(toClient);
    } else {
      userId = (String) emitter;
    }
    this.usersPanel.removeEntry(this.users.get(userId));
    this.users.remove(userId);

  }

  
}
