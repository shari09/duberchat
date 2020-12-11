package client;

import java.util.LinkedHashSet;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.Box;
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
import common.entities.UserMetadata;
import common.entities.payload.CreateChannel;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class MainUserFrame extends DisconnectOnCloseFrame implements ActionListener {

  public static final int WIDTH = 400;
  public static final int HEIGHT = 800;

  private ClientSocket clientSocket;
  private UserChatFrame chatFrame;

  private UserProfilePanel userProfilePanel;
  private JTextField channelSearchBar; // search by channel name

  private JList<ChannelMetadata> channelsList;

  private JButton notifInboxButton;
  private JButton friendsButton;
  private JButton settingsButton;
  private JButton createGroupChannelButton;

  public MainUserFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.clientSocket = clientSocket;

    this.setSize(MainUserFrame.WIDTH, MainUserFrame.HEIGHT);
    this.setResizable(false);

    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel northPanel = new JPanel();
    northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));
    
    // user's profile section
    this.userProfilePanel = new UserProfilePanel();
    this.userProfilePanel.setMaximumSize(new Dimension(MainUserFrame.WIDTH, MainUserFrame.HEIGHT/10));
    this.userProfilePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    northPanel.add(this.userProfilePanel);

    // channel search bar
    //TODO: add listener to search bar
    this.channelSearchBar = new JTextField(20);
    this.channelSearchBar.setMaximumSize(new Dimension(MainUserFrame.WIDTH, MainUserFrame.HEIGHT/20));
    northPanel.add(this.channelSearchBar);

    contentPane.add(northPanel, BorderLayout.NORTH);
    
    // a scrollable list of channels
    DefaultListModel<ChannelMetadata> listModel = new DefaultListModel<>();
    synchronized (GlobalClient.clientData) {
      LinkedHashSet<ChannelMetadata> channelsMetadata = GlobalClient.clientData.getChannels();
      for (ChannelMetadata curMetadata: channelsMetadata) {
        listModel.addElement(curMetadata);
      }
    }
    this.channelsList = new JList<ChannelMetadata>(listModel);
    contentPane.add(new JScrollPane(this.channelsList), BorderLayout.CENTER);
    
    // buttons, at the bottom
    // TODO: replace text with icon, add listener
    this.notifInboxButton = new JButton("notif");
    this.notifInboxButton.addActionListener(this);

    this.friendsButton = new JButton("friends");
    this.friendsButton.addActionListener(this);

    this.settingsButton = new JButton("settings");
    this.settingsButton.addActionListener(this);

    this.createGroupChannelButton = new JButton("new group chat");
    this.createGroupChannelButton.addActionListener(this);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(this.notifInboxButton);
    buttonPanel.add(this.friendsButton);
    buttonPanel.add(this.settingsButton);
    buttonPanel.add(this.createGroupChannelButton);
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
        System.out.println(new CreateChannel(
          1,
          data.getToken(),
          data.getUserId(),
          new LinkedHashSet<UserMetadata>(),
          channelName
        ).getUserId());
        this.clientSocket.sendPayload(
          new CreateChannel(
            1,
            data.getToken(),
            data.getUserId(),
            new LinkedHashSet<UserMetadata>(),
            channelName
          )
        );
      }
    }
  }
}
