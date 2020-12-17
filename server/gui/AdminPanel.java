package server.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import common.entities.UserMetadata;
import common.gui.Theme;
import server.entities.Client;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.Subscribable;

/**
 * 
 * <p>
 * Created on 2020.12.13.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AdminPanel extends JPanel implements Subscribable, ActionListener {

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

  private JButton actionButton;
  private JTextArea msg;

  private GridBagConstraints userC;
  private EventType eventType;

  private String action;


  public AdminPanel(String action, String title, EventType eventType) {
    super();
    this.userToCheckBox = new LinkedHashMap<>();
    this.checkBoxToUser = new LinkedHashMap<>();
    this.clients = new LinkedHashMap<>();
    this.eventType = eventType;
    this.action = action;

    this.setLayout(new GridBagLayout());
    
    JPanel titlePanel = ServerGUIFactory.getHeader(title);
    GridBagConstraints overallC = new GridBagConstraints();
    
    //title
    overallC.gridx = 0;
    overallC.gridy = 0;
    overallC.weightx = 10;
    overallC.weighty = 0;
    overallC.gridwidth = 2;
    overallC.fill = GridBagConstraints.BOTH;
    overallC.anchor = GridBagConstraints.NORTH;

    this.add(titlePanel, overallC);

    //select all
    this.selectAll = ServerGUIFactory.getCheckBox(
      "Select all", ServerGUIFactory.EMPHASIS_TEXT, 18,
      50, 12,
      ServerGUIFactory.EMPHASIS,
      ServerGUIFactory.EMPHASIS_HOVER,
      true
    );
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
    this.usersPanel.setBackground(ServerGUIFactory.USER_SELECTION);
    this.usersPanel.setAlignmentX(LEFT_ALIGNMENT);
    
    this.userC = ServerGUIFactory.getScrollConstraints();
    this.usersPanel.add(Box.createVerticalGlue(), this.userC);

    this.userC.weighty = 0;
    
    this.scrollPane = ServerGUIFactory.getScrollPane(this.usersPanel);


    overallC.gridx = 0;
    overallC.gridy = 2;
    overallC.weightx = 0;
    overallC.weighty = 1;
    overallC.gridwidth = 1;
    
    this.add(this.scrollPane, overallC);

    //message
    this.msg = new JTextArea("");
    this.msg.setFont(Theme.getPlainFont(15));
    this.msg.setLineWrap(true);
    this.msg.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    this.msg.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    this.msg.setForeground(ServerGUIFactory.GENERAL_TEXT);
    this.msg.setCaretColor(ServerGUIFactory.GENERAL_TEXT);

    JScrollPane scroll = new JScrollPane(this.msg);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getVerticalScrollBar().setUI(ServerGUIFactory.getScrollbarUI());
    
    overallC.weightx = 1;
    overallC.gridx = 1;
    overallC.gridy = 1;
    overallC.gridheight = 2;

    this.add(scroll, overallC);


    //action button
    this.actionButton = ServerGUIFactory.getButton(
      this.action, ServerGUIFactory.ADMIN_BUTTON_TEXT, 30,
      40, 30, 
      ServerGUIFactory.ADMIN_BUTTON, 
      ServerGUIFactory.ADMIN_BUTTON_HOVER
    );
    this.actionButton.setFont(Theme.getBoldFont(30));

    this.actionButton.addActionListener(this);

    overallC.gridx = 0;
    overallC.gridy = 3;
    overallC.gridwidth = 2;
    overallC.weighty = 0;

    this.add(this.actionButton, overallC);
    
    this.activate();
  }

  

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
    GlobalServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
    GlobalServices.guiEventQueue.subscribe(EventType.PROFILE_CHANGE, this);
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
      case PROFILE_CHANGE:
        this.updateUser(emitter);
        break;
      default:
        break;
    }
  }

  private void updateUser(Object emitter) {
    UserMetadata user = (UserMetadata)emitter;
    JCheckBox box = this.getUserCheckBox(user.getUsername());
    JCheckBox old = this.userToCheckBox.get(user.getUserId());
    int index = Arrays.asList(this.usersPanel.getComponents()).indexOf(old);
    this.userToCheckBox.put(user.getUserId(), box);
    this.checkBoxToUser.remove(old);
    this.usersPanel.remove(old);
    this.usersPanel.add(box, this.userC, index);
    this.repaint();
  }

  private void addUser(Object emitter) {
    Client client = (Client)emitter;
    String userId = client.getUserId();
    this.clients.put(userId, client);

    JCheckBox box = this.getUserCheckBox(
      GlobalServices.users.getUsername(userId)
    );

    this.userToCheckBox.put(userId, box);
    this.checkBoxToUser.put(box, userId);
    this.usersPanel.add(box, this.userC, this.clients.size()-1);
    this.repaint();
  }

  private JCheckBox getUserCheckBox(String username) {
    JCheckBox box = ServerGUIFactory.getCheckBox(
      username, 
      ServerGUIFactory.USER_TEXT,
      15,
      8, 5,
      ServerGUIFactory.USER_SELECTION,
      ServerGUIFactory.USER_ACTIVE,
      false
    );
    box.setFont(Theme.getPlainFont(13));
    box.setHorizontalAlignment(SwingConstants.LEFT);
    box.addActionListener(this);
    return box;
  }

  private void removeUser(Object emitter) {
    String userId;
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      userId = GlobalServices.clientConnections.getUserId(toClient);
    } else {
      userId = (String) emitter;
    }
    if (!this.clients.containsKey(userId)) {
      return;
    }
    this.clients.remove(userId);
    this.usersPanel.remove(this.userToCheckBox.get(userId));
    this.checkBoxToUser.remove(this.userToCheckBox.get(userId));
    this.userToCheckBox.remove(userId);
    GlobalServices.serverEventQueue.emitEvent(
      EventType.REMOVE_CLIENT_CONNECTION, 
      2, 
      userId
    );
    this.repaint();
  }


  public String getMessageText() {
    return this.msg.getText();
  }

  public void setMessageText(String text) {
    this.msg.setText(text);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.selectAll) {
      this.toggleSelection(this.selectAll, this.userToCheckBox.values()); 
    } else if (e.getSource() == this.actionButton) {
      GlobalServices.guiEventQueue.emitEvent(
        this.eventType, 3, this.actionButton 
      );
    } else {
      this.selectAll.setSelected(false);
    }

  }

  public ArrayList<ObjectOutputStream> getSelectedUsersOut() {
    ArrayList<ObjectOutputStream> users = new ArrayList<>();
    for (JCheckBox user: this.userToCheckBox.values()) {
      if (user.isSelected()) {
        ObjectOutputStream out = this.clients
          .get(this.checkBoxToUser.get(user))
          .getClient();
        users.add(out);
      }
    }
    return users;
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
