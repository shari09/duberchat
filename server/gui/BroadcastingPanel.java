package server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JCheckBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import server.entities.Client;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.Subscribable;

public class BroadcastingPanel extends JPanel implements Subscribable, ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private LinkedHashMap<String, JCheckBox> userCheckBoxs;
  private LinkedHashMap<String, Client> clients;

  private JPanel usersPanel;
  private JScrollPane scrollPane;

  private GridBagConstraints c;


  public BroadcastingPanel() {
    super();
    this.userCheckBoxs = new LinkedHashMap<>();
    this.clients = new LinkedHashMap<>();

    this.setLayout(new GridBagLayout());
    JPanel title = ComponentsFactory.getHeader(
      "Broadcasting", Style.GRAY2
    );
    GridBagConstraints overallC = new GridBagConstraints();
    overallC.gridx = 0;
    overallC.gridy = 0;
    overallC.weightx = 1;

    this.add(title, overallC);

    

    // this.usersPanel = new JPanel();
    // this.usersPanel.setLayout(new GridBagLayout());
    // this.usersPanel.setBorder(BorderFactory.createEmptyBorder());
    // this.usersPanel.setBackground(Color.WHITE);




    // this.c = ComponentsFactory.getScrollConstraints();

    // this.c.weighty = 0;
    

    this.activate();
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
    GlobalServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    switch (eventType) {
      case AUTHENTICATED_CLIENT:
        this.addUser(emitter);
        break;
      case CLIENT_DISCONNECTED:
        this.removeUser(emitter);
        break;
      default:
        break;
    }
  }

  private void addUser(Object emitter) {
    Client client = (Client)emitter;
    String userId = client.getUserId();
    this.clients.put(userId, client);
    JCheckBox box = new JCheckBox(GlobalServices.users.getUsername(userId));
    this.userCheckBoxs.put(userId, box);

  }

  private void removeUser(Object emitter) {
    String userId;
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      userId = GlobalServices.clientConnections.getUserId(toClient);
    } else {
      userId = (String) emitter;
    }
    
    this.clients.remove(userId);
    this.userCheckBoxs.remove(userId);
  }

  private JPanel getTextField() {
    JTextArea text = new JTextArea();
    JButton button = new JButton();

    JPanel panel = new JPanel();
    return panel;

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

  }
  
}
