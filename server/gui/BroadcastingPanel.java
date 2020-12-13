package server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.swing.JCheckBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.SwingConstants;

import common.entities.payload.Payload;
import common.entities.payload.ServerBroadcast;
import server.entities.Client;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.PayloadSender;
import server.services.Subscribable;

public class BroadcastingPanel extends JPanel implements Subscribable, ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private LinkedHashMap<String, JCheckBox> userToCheckBox;
  private LinkedHashMap<JCheckBox, String> checkBoxToUser;
  private LinkedHashMap<String, Client> clients;

  private JPanel usersPanel;
  private JScrollPane scrollPane;

  private JCheckBox selectAll;

  private JButton broadcast;
  private JTextArea msg;

  private GridBagConstraints c;


  public BroadcastingPanel() {
    super();
    this.userToCheckBox = new LinkedHashMap<>();
    this.checkBoxToUser = new LinkedHashMap<>();
    this.clients = new LinkedHashMap<>();

    this.setLayout(new GridBagLayout());
    
    JPanel title = Components.getHeader(
      "Broadcasting", Components.GRAY2
    );
    GridBagConstraints overallC = new GridBagConstraints();
    
    //title
    overallC.gridx = 0;
    overallC.gridy = 0;
    overallC.weightx = 10;
    overallC.weighty = 0;
    overallC.gridwidth = 2;
    overallC.fill = GridBagConstraints.BOTH;
    overallC.anchor = GridBagConstraints.NORTH;

    this.add(title, overallC);

    //select all
    //TODO: get a better icon
    this.selectAll = Components.getCheckBox(
      "Select all", 
      40, 10,
      Components.DARK_PURPLE,
      Components.DARK_PURPLE_OVERLAY,
      true
    );
    this.selectAll.setForeground(Components.LIGHT_TEXT);
    this.selectAll.addActionListener(this);

    overallC.gridx = 0;
    overallC.gridy = 1;
    overallC.weightx = 0;
    overallC.weighty = 0;
    overallC.gridwidth = 1;
    this.add(selectAll, overallC);

    //users panel
    this.usersPanel = new JPanel();
    this.usersPanel.setMinimumSize(this.usersPanel.getPreferredSize());
    this.usersPanel.setLayout(new GridBagLayout());
    this.usersPanel.setBackground(Components.LIGHT_PURPLE);
    this.usersPanel.setAlignmentX(LEFT_ALIGNMENT);
    
    this.c = Components.getScrollConstraints();
    this.usersPanel.add(Box.createVerticalGlue(), this.c);

    this.c.weighty = 0;
    
    this.scrollPane = Components.getScrollPane(this.usersPanel, true);


    overallC.gridx = 0;
    overallC.gridy = 2;
    overallC.weightx = 0;
    overallC.weighty = 1;
    overallC.gridwidth = 1;
    
    this.add(this.scrollPane, overallC);

    //broadcasting panel
    overallC.gridx = 1;
    overallC.gridy = 1;
    overallC.gridheight = 2;


    this.add(this.getBroadcastingPane(), overallC);

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
    JCheckBox box = Components.getCheckBox(
      GlobalServices.users.getUsername(userId),
      // userId,
      8, 5,
      Components.LIGHT_PURPLE,
      Components.LIGHT_PURPLE2,
      false
    );
    box.setFont(Components.getFont(13));
    box.setHorizontalAlignment(SwingConstants.LEFT);
    box.addActionListener(this);

    this.userToCheckBox.put(userId, box);
    this.checkBoxToUser.put(box, userId);
    this.usersPanel.add(box, this.c, this.clients.size()-1);
    this.repaint();
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
    this.usersPanel.remove(this.userToCheckBox.get(userId));
    this.checkBoxToUser.remove(this.userToCheckBox.get(userId));
    this.userToCheckBox.remove(userId);
    this.repaint();
  }


  private JPanel getBroadcastingPane() {
    //msg
    this.msg = new JTextArea("Message to broadcast");
    this.msg.setFont(Components.getFont(15));
    this.msg.setLineWrap(true);
    this.msg.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane scroll = new JScrollPane(this.msg);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    
    //button
    this.broadcast = Components.getButton(
      "Broadcast", Components.LIGHT_TEXT, 20,
      40, 30, 
      Components.DARK_PURPLE, 
      Components.DARK_PURPLE_OVERLAY
    );

    this.broadcast.addActionListener(this);
    

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(this.broadcast, BorderLayout.PAGE_END);
    


    return panel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.selectAll) {
      this.toggleSelection(this.selectAll, this.userToCheckBox.values()); 
    } else if (e.getSource() == this.broadcast) {
      this.broadcastMsg(this.userToCheckBox.values());
      this.msg.setText("Message to broadcast");
    } else {
      this.selectAll.setSelected(false);
    }

  }


  private void broadcastMsg(Collection<JCheckBox> users) {
    for (JCheckBox user: users) {
      if (user.isSelected()) {
        ObjectOutputStream out = this.clients
          .get(this.checkBoxToUser.get(user))
          .getClient();
        PayloadSender.send(out, new ServerBroadcast(this.msg.getText()));
      }
    }
  }

  private void toggleSelection(
    JCheckBox selectAll, 
    Collection<JCheckBox> boxes
  ) {
    if (selectAll.isSelected()) {
      for (JCheckBox box : boxes) {
        box.setSelected(true);
      }
      return;
    }

    if (this.allSelected(boxes)) {
      for (JCheckBox box : boxes) {
        box.setSelected(false);
      }
    }
  }

  private boolean allSelected(Collection<JCheckBox> boxes) {
    for (JCheckBox box: boxes) {
      if (!box.isSelected()) {
        return false;
      }
    }
    return true;
  }
  
}
