package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.resources.GlobalPayloadQueue;
import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.Constants;
import common.entities.GroupChannelMetadata;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.CreateChannel;
import common.entities.payload.server_to_client.ServerBroadcast;
import common.gui.Theme;

/**
 * The main frame of the user, which includes
 * a list of private and group channels,
 * button to create a new channel,
 * and buttons to navigate to the friends frame and settings frame.
 * <p>
 * Created on 2020.12.09.
 * 
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserMainFrame extends DisconnectOnCloseFrame implements ActionListener,
                                                                     MouseListener {

  public static final Dimension DIMENSION = new Dimension(450, 850);
  
  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.CREATE_CHANNEL,
    PayloadType.CHANGE_CHANNEL,
    PayloadType.REMOVE_PARTICIPANT,
    PayloadType.ADD_PARTICIPANT,
    PayloadType.BLACKLIST_USER,
    PayloadType.LEAVE_CHANNEL,
    PayloadType.TRANSFER_OWNERSHIP
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.KEEP_ALIVE,
    PayloadType.CREATE_CHANNEL,
    PayloadType.CHANGE_CHANNEL,
    PayloadType.REMOVE_PARTICIPANT,
    PayloadType.ADD_PARTICIPANT,
    PayloadType.BLACKLIST_USER,
    PayloadType.LEAVE_CHANNEL,
    PayloadType.TRANSFER_OWNERSHIP
  };

  private UserChatFrame chatFrame;
  private UserFriendsFrame friendsFrame;
  private UserSettingsFrame settingsFrame;

  // private JPanel userProfilePanel;
  private JLabel usernameLabel;
  private JLabel statusLabel;

  private JList<PrivateChannelMetadata> privateChannelsList;
  private JList<GroupChannelMetadata> groupChannelsList;

  private JButton createGroupChannelButton;
  private JButton friendsFrameButton;
  private JButton settingsButton;

  public UserMainFrame(ClientSocket clientSocket) {
    super(clientSocket);

    this.setSize(UserMainFrame.DIMENSION);
    this.setResizable(false);

    this.chatFrame = new UserChatFrame(clientSocket);
    this.friendsFrame = new UserFriendsFrame(clientSocket);
    this.settingsFrame = new UserSettingsFrame(clientSocket);
    this.settingsFrame.setVisible(false);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    // user's profile section
    JPanel profilePanel = this.getProfilePanel();
    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.gridwidth = 5;
    constraints.gridheight = 2;
    panel.add(profilePanel, constraints);
    constraints.weighty = 1;
    // tabbed pane for channels
    this.privateChannelsList = new JList<PrivateChannelMetadata>();
    this.privateChannelsList.setCellRenderer(new ChannelThumbnailRenderer());
    this.privateChannelsList.addMouseListener(this);
    this.groupChannelsList = new JList<GroupChannelMetadata>();
    this.groupChannelsList.setCellRenderer(new ChannelThumbnailRenderer());
    this.groupChannelsList.addMouseListener(this);
    this.updateChannelsJLists();

    JPanel privateChannelPanel = new JPanel(new BorderLayout());
    // friends page button
    this.friendsFrameButton = ClientGUIFactory.getTextButton(
      "manage friends",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_3,
      ClientGUIFactory.BLUE_SHADE_1
    );
    this.friendsFrameButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    this.friendsFrameButton.addActionListener(this);
    privateChannelPanel.add(this.friendsFrameButton, BorderLayout.NORTH);
    // a scrollable list of private channels
    JScrollPane pcScrollPane = ClientGUIFactory.getScrollPane(this.privateChannelsList);
    pcScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    privateChannelPanel.add(pcScrollPane, BorderLayout.CENTER);

    JPanel groupChannelPanel = new JPanel(new BorderLayout());
    // new group chat button
    this.createGroupChannelButton = ClientGUIFactory.getTextButton(
      "+ new group chat",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_3,
      ClientGUIFactory.BLUE_SHADE_1
    );
    this.createGroupChannelButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    this.createGroupChannelButton.addActionListener(this);
    groupChannelPanel.add(this.createGroupChannelButton, BorderLayout.NORTH);
    // a scrollable list of group channels
    JScrollPane gcScrollPane = ClientGUIFactory.getScrollPane(this.groupChannelsList);
    gcScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    groupChannelPanel.add(gcScrollPane, BorderLayout.CENTER);

    JTabbedPane tabbedPane = ClientGUIFactory.getTabbedPane(Theme.getBoldFont(20));
    tabbedPane.addTab("friends", privateChannelPanel);
    tabbedPane.addTab("group chats", groupChannelPanel);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 6;
    constraints.gridheight = 12;
    panel.add(tabbedPane, constraints);

    // button
    constraints.weightx = 0;
    constraints.weighty = 0;
    this.settingsButton = ClientGUIFactory.getImageButton(ClientGUIFactory.getSettingsIcon(50, 50));
    this.settingsButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 20));
    this.settingsButton.addActionListener(this);

    constraints.gridx = 5;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    panel.add(this.settingsButton, constraints);

    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  private JPanel getProfilePanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(50, 50));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    this.usernameLabel = ClientGUIFactory.getTextLabel(
      GlobalClient.clientData.getUsername(),
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_3
    );
    panel.add(this.usernameLabel, constraints);

    this.statusLabel = ClientGUIFactory.getTextLabel(
      ClientGUIFactory.getStatusText(GlobalClient.clientData.getStatus()),
      Theme.getItalicFont(15),
      ClientGUIFactory.getStatusColor(GlobalClient.clientData.getStatus())
    );
    constraints.gridy = 1;
    panel.add(this.statusLabel, constraints);
    return panel;
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return UserMainFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return UserMainFrame.ERROR_NOTIF_TYPES;
  }

  @Override
  public void actionPerformed(ActionEvent e) { 
    if (e.getSource() == this.createGroupChannelButton) {
      String channelName = (String)(JOptionPane.showInputDialog(
        this,
        "Channel name: ",
        "Create Channel",
        JOptionPane.QUESTION_MESSAGE,
        ClientGUIFactory.getDialogInformationIcon(30, 30),
        null,
        null
      ));
      
      if ((channelName == null) || (channelName.length() == 0)) {
        return;
      }

      if ((!Constants.NAME_VALIDATOR.matches(channelName))) {
        GlobalJDialogPrompter.warnInvalidInput(
          this,
          "Channel name",
          Constants.NAME_VALIDATOR
        );
        return;
      }

      synchronized (GlobalClient.clientData) {
        ClientData data = GlobalClient.clientData;
        LinkedHashSet<UserMetadata> participants = new LinkedHashSet<UserMetadata>();
        participants.add(
          new UserMetadata(
            data.getUserId(),
            data.getUsername(),
            data.getDescription(),
            data.getStatus()
          )
        );
        GlobalPayloadQueue.enqueuePayload(
          new CreateChannel(
            1,
            data.getToken(),
            data.getUserId(),
            participants,
            channelName
          )
        );
      }

    } else if (e.getSource() == this.friendsFrameButton) {
      if (!this.friendsFrame.isVisible()) {
        this.friendsFrame.setVisible(true);
        this.friendsFrame.setLocationRelativeTo(this);
      }
      this.friendsFrame.requestFocus();

    } else if (e.getSource() == this.settingsButton) {
      if (!this.settingsFrame.isVisible()) {
        this.settingsFrame.setVisible(true);
        this.settingsFrame.setLocationRelativeTo(this);
      }
      this.settingsFrame.requestFocus();
    }
  }
  
  @Override
  public void clientDataUpdated() {
    this.updateUserProfilePanel();
    this.updateChannelsJLists();
    this.repaint();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
    this.requestFocus();
    synchronized (broadcast) {
      JOptionPane.showMessageDialog(
        this,
        broadcast.getMessage(),
        "!Important Message From Server!",
        JOptionPane.WARNING_MESSAGE,
        ClientGUIFactory.getDialogBroadcastIcon(30, 30)
      );
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // group channels
    if (e.getSource() == this.groupChannelsList) {
      int row = this.groupChannelsList.locationToIndex(e.getPoint());
      this.groupChannelsList.setSelectedIndex(row);
      GroupChannelMetadata metadata = this.groupChannelsList.getSelectedValue();
      if (metadata == null) {
        return;
      }
      // open chat
      if (SwingUtilities.isLeftMouseButton(e)) {
        if (!this.chatFrame.hasChannelTab(metadata.getChannelId())) {
          this.chatFrame.addChannel(metadata.getChannelId());
        }
        if (!this.chatFrame.isVisible()) {
          this.chatFrame.setVisible(true);
          this.chatFrame.setLocationRelativeTo(this);
        }
        this.chatFrame.requestFocus();
        this.privateChannelsList.setSelectedValue(null, false);
      
      // group chat actions
      } else if (SwingUtilities.isRightMouseButton(e)) {
        GlobalJDialogPrompter.promptGroupChannelAction(this, metadata);
      }

    // private channels
    } else if (e.getSource() == this.privateChannelsList) {
      PrivateChannelMetadata metadata = this.privateChannelsList.getSelectedValue();
      if (metadata == null) {
        return;
      }
      // open chat
      if (SwingUtilities.isLeftMouseButton(e)) {
        if ((metadata != null) && (!this.chatFrame.hasChannelTab(metadata.getChannelId()))) {
          this.chatFrame.addChannel(metadata.getChannelId());
        }
        if (!this.chatFrame.isVisible()) {
          this.chatFrame.setVisible(true);
          this.chatFrame.setLocationRelativeTo(this);
        }
        this.chatFrame.requestFocus();
        this.privateChannelsList.setSelectedValue(null, false);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }
  @Override
  public void mouseClicked(MouseEvent e) {
  }
  @Override
  public void mouseEntered(MouseEvent e) {
  }
  @Override
  public void mouseExited(MouseEvent e) {
  }
  
  private void updateUserProfilePanel() {
    this.usernameLabel.setText(GlobalClient.getClientUserMetadata().getUsername());
    this.statusLabel.setText(ClientGUIFactory.getStatusText(GlobalClient.getClientUserMetadata().getStatus()));
    this.statusLabel.setForeground(ClientGUIFactory.getStatusColor(GlobalClient.getClientUserMetadata().getStatus()));
    this.usernameLabel.repaint();
    this.statusLabel.repaint();
    this.revalidate();
    this.repaint();
  }

  private void updateChannelsJLists() {
    DefaultListModel<GroupChannelMetadata> groupChannelsListModel = new DefaultListModel<>();
    DefaultListModel<PrivateChannelMetadata> privateChannelsListModel = new DefaultListModel<>();
    LinkedHashSet<ChannelMetadata> channelsMetadata = GlobalClient.clientData.getChannels();
    synchronized (channelsMetadata) {
      for (ChannelMetadata curMetadata: channelsMetadata) {
        if (curMetadata instanceof GroupChannelMetadata) {
          groupChannelsListModel.addElement((GroupChannelMetadata)curMetadata);
        } else if (curMetadata instanceof PrivateChannelMetadata) {
          privateChannelsListModel.addElement((PrivateChannelMetadata)curMetadata);
        }
      }
    }
    this.groupChannelsList.setModel(groupChannelsListModel);
    this.groupChannelsList.revalidate();
    this.privateChannelsList.setModel(privateChannelsListModel);
    this.privateChannelsList.revalidate();
    
  }
}
