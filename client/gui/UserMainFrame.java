package client.gui;

import java.util.LinkedHashSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Container;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

import common.entities.ChannelMetadata;
import common.entities.Constants;
import common.entities.payload.ServerBroadcast;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.CreateChannel;
import common.entities.GroupChannelMetadata;
import common.entities.payload.PayloadType;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.services.ChannelServices;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserMainFrame extends DisconnectOnCloseFrame implements ActionListener,
                                                                     MouseListener {

  public static final Dimension DIMENSION = new Dimension(400, 800);
  
  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.CREATE_CHANNEL
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.KEEP_ALIVE,
    PayloadType.CREATE_CHANNEL
  };

  private UserChatFrame chatFrame;
  private UserFriendsFrame friendsFrame;
  private UserSettingsFrame settingsFrame;

  private UserProfilePanel userProfilePanel;

  private JList<PrivateChannelMetadata> privateChannelsList;
  private JList<GroupChannelMetadata> groupChannelsList;

  private JButton createGroupChannelButton;
  private JButton friendsFrameButton;
  private JButton settingsButton;

  public UserMainFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.setSize(UserMainFrame.DIMENSION);
    this.setResizable(false);

    this.chatFrame = new UserChatFrame("Chat Window", clientSocket);
    this.chatFrame.setVisible(false);

    this.friendsFrame = new UserFriendsFrame("Friends", clientSocket);
    this.friendsFrame.setVisible(false);

    this.settingsFrame = new UserSettingsFrame("Settings", clientSocket);
    this.settingsFrame.setVisible(false);

    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());
    
    // user's profile section
    this.updateUserProfilePanel();
    contentPane.add(this.userProfilePanel, BorderLayout.NORTH);
    
    // card layout for private / group chats display
    this.privateChannelsList = new JList<PrivateChannelMetadata>();
    this.privateChannelsList.addMouseListener(this);
    this.groupChannelsList = new JList<GroupChannelMetadata>();
    this.groupChannelsList.addMouseListener(this);
    this.updateChannelsJLists();

    JPanel privateChannelPanel = new JPanel(new BorderLayout());
    // friends page button
    this.friendsFrameButton = new JButton("manage friends");
    this.friendsFrameButton.addActionListener(this);
    privateChannelPanel.add(this.friendsFrameButton, BorderLayout.NORTH);
    // a scrollable list of private channels
    JScrollPane pcScrollPane = new JScrollPane(this.privateChannelsList);
    pcScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    pcScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    privateChannelPanel.add(pcScrollPane, BorderLayout.CENTER);

    JPanel groupChannelPanel = new JPanel(new BorderLayout());
    // new group chat button
    this.createGroupChannelButton = new JButton("new group chat");
    this.createGroupChannelButton.addActionListener(this);
    groupChannelPanel.add(this.createGroupChannelButton, BorderLayout.NORTH);
    // a scrollable list of group channels
    JScrollPane gcScrollPane = new JScrollPane(this.groupChannelsList);
    gcScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    gcScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    groupChannelPanel.add(gcScrollPane, BorderLayout.CENTER);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("friends", privateChannelPanel);
    tabbedPane.addTab("group chats", groupChannelPanel);
    contentPane.add(tabbedPane, BorderLayout.CENTER);

    // buttons, at the bottom
    // TODO: replace text with icon
    this.settingsButton = new JButton("settings");
    this.settingsButton.addActionListener(this);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(this.settingsButton);

    JButton test = new JButton("test");
    test.addActionListener(
      new ActionListener() {
        @Override
        public synchronized void actionPerformed(ActionEvent e) {
          GlobalClient.displayClientData();
        }
      }
    );
    buttonPanel.add(test);

    contentPane.add(buttonPanel, BorderLayout.PAGE_END);
  
    this.setVisible(true);
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
      // TODO: also ask for participants
      String channelName = JOptionPane.showInputDialog(this, "Channel Name: ");
      
      if ((channelName == null) || (channelName.length() == 0)) {
        return;
      }

      if ((!Constants.NAME_VALIDATOR.matches(channelName))) {
        JOptionPane.showMessageDialog(
          this,
          "Channel name does not meet requirements:"
          + "\n" + Constants.NAME_VALIDATOR.getDescription(),
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE
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
        this.getClientSocket().sendPayload(
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
      this.friendsFrame.setVisible(true);
      this.friendsFrame.requestFocus();

    } else if (e.getSource() == this.settingsButton) {
      this.settingsFrame.setVisible(true);
      this.settingsFrame.requestFocus();
    }
  }
  
  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
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
        JOptionPane.WARNING_MESSAGE
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
        this.chatFrame.setVisible(true);
        this.chatFrame.requestFocus();
        this.privateChannelsList.setSelectedValue(null, false);
      
      // group chat actions
      } else if (SwingUtilities.isRightMouseButton(e)) {
        GlobalJDialogPrompter.promptGroupChannelAction(
          this,
          metadata,
          this.getClientSocket()
        );
      }

    // private channels
    } else if (e.getSource() == this.privateChannelsList) {
      int row = this.privateChannelsList.locationToIndex(e.getPoint());
      this.privateChannelsList.setSelectedIndex(row);
      PrivateChannelMetadata metadata = this.privateChannelsList.getSelectedValue();
      if (metadata == null) {
        return;
      }
      // open chat
      if (SwingUtilities.isLeftMouseButton(e)) {
        if ((metadata != null) && (!this.chatFrame.hasChannelTab(metadata.getChannelId()))) {
          this.chatFrame.addChannel(metadata.getChannelId());
        }
        this.chatFrame.setVisible(true);
        this.chatFrame.requestFocus();
        this.privateChannelsList.setSelectedValue(null, false);

      // friend actions
      } else if (SwingUtilities.isRightMouseButton(e)) {
        GlobalJDialogPrompter.promptFriendAction(
          this,
          ChannelServices.getOtherUserInPrivateChannel(metadata),
          this.getClientSocket()
        );
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
    this.userProfilePanel = new UserProfilePanel();
    this.userProfilePanel.setMaximumSize(new Dimension(UserMainFrame.WIDTH, UserMainFrame.HEIGHT/10));
    this.userProfilePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    this.userProfilePanel.revalidate();
  }

  private synchronized void updateChannelsJLists() {
    DefaultListModel<GroupChannelMetadata> groupChannelsListModel = new DefaultListModel<>();
    DefaultListModel<PrivateChannelMetadata> privateChannelsListModel = new DefaultListModel<>();
    synchronized (GlobalClient.clientData) {
      LinkedHashSet<ChannelMetadata> channelsMetadata = GlobalClient.clientData.getChannels();
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
