package client.gui;

import java.util.LinkedHashSet;
import java.io.IOException;
import java.security.acl.Group;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Container;
import java.awt.CardLayout;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;
import common.entities.payload.CreateChannel;
import common.entities.GroupChannelMetadata;
import common.entities.payload.PayloadType;

import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class MainUserFrame extends DisconnectOnCloseFrame implements ActionListener,
                                                                     ClientSocketListener,
                                                                     MouseMotionListener {

  public static final int WIDTH = 400;
  public static final int HEIGHT = 800;

  private ClientSocket clientSocket;
  private UserChatFrame chatFrame;

  private UserProfilePanel userProfilePanel;

  private JList<PrivateChannelMetadata> privateChannelsList;
  private JList<GroupChannelMetadata> groupChannelsList;

  private JButton createGroupChannelButton;
  private JButton friendsFrameButton;
  private JButton settingsButton;

  public MainUserFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.clientSocket = clientSocket;

    this.setSize(MainUserFrame.WIDTH, MainUserFrame.HEIGHT);
    this.setResizable(false);
    this.addMouseMotionListener(this);

    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());
    
    // user's profile section
    this.updateUserProfilePanel();
    contentPane.add(this.userProfilePanel, BorderLayout.NORTH);
    
    // card layout for private / group chats display
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
    // TODO: replace text with icon, add listener
    this.settingsButton = new JButton("settings");
    this.settingsButton.addActionListener(this);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(this.settingsButton);
    contentPane.add(buttonPanel, BorderLayout.PAGE_END);

    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) { 
    if (e.getSource() == this.createGroupChannelButton) {
      // TODO: also ask for participants
      String channelName = JOptionPane.showInputDialog("Channel Name: ");
      if ((channelName == null) || (channelName.length() == 0)) {
        return;
      }

      
      synchronized (GlobalClient.clientData) {
        ClientData data = GlobalClient.clientData;
        LinkedHashSet<UserMetadata> participants = new LinkedHashSet<UserMetadata>();
        participants.add(new UserMetadata(
          data.getUserId(),
          data.getUsername(), 
          data.getDescription(), 
          data.getStatus()
        ));
        this.clientSocket.sendPayload(
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
      UserFriendsFrame friendsFrame = new UserFriendsFrame("Friends", this.getClientSocket());
    } else if (e.getSource() == this.settingsButton) {
      UserSettingsFrame settingsFrame = new UserSettingsFrame("Settings", this.getClientSocket());
    }
  }
  
  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    this.updateChannelsJLists();
  }

  @Override
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {
    if (successful) {
      System.out.println(notifMessage);
      return;
    }
    if (payloadType == PayloadType.CREATE_CHANNEL) {
      JOptionPane.showMessageDialog(this, notifMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    this.getClientSocket().updateLastActiveTime();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    this.getClientSocket().updateLastActiveTime();
  }

  private void updateUserProfilePanel() {
    this.userProfilePanel = new UserProfilePanel();
    this.userProfilePanel.setMaximumSize(new Dimension(MainUserFrame.WIDTH, MainUserFrame.HEIGHT/10));
    this.userProfilePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
  }

  private void updateChannelsJLists() {
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
    this.groupChannelsList = new JList<GroupChannelMetadata>(groupChannelsListModel);
    this.privateChannelsList = new JList<PrivateChannelMetadata>(privateChannelsListModel);
  }
}
