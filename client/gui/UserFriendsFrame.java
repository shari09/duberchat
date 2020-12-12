package client.gui;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
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
import javax.jws.soap.SOAPBinding.Use;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.entities.payload.FriendRequestToServer;
import common.entities.GroupChannelMetadata;
import common.entities.payload.PayloadType;

import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;

@SuppressWarnings("serial")
public class UserFriendsFrame extends UserFrame implements ActionListener {
  private static final Dimension PREFERRED_DIMENSION = new Dimension(800, 600);

  private JList<UserMetadata> friends;
  private JList<UserMetadata> onlineFriends;
  private JList<IncomingFriendRequest> incomingFriendRequests;
  private JList<OutgoingFriendRequest> outgoingFriendRequests;
  private JList<UserMetadata> blocked;
  private JTextField usernameSearchField;
  private JTextField requestMessageField;
  private JButton sendFriendRequestButton;

  public UserFriendsFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);
    
    this.setSize(UserFriendsFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserFriendsFrame.PREFERRED_DIMENSION);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(true);

    this.updateJLists(GlobalClient.clientData);
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab(
      "Online",
      new JScrollPane(this.onlineFriends)
    );
    tabbedPane.addTab(
      "All",
      new JScrollPane(this.friends)
    );
    tabbedPane.addTab(
      "Incoming Friend Requests",
      new JScrollPane(this.incomingFriendRequests)
    );
    tabbedPane.addTab(
      "Outgoing Friend Requests",
      new JScrollPane(this.outgoingFriendRequests)
    );
    tabbedPane.addTab(
      "Blocked",
      new JScrollPane(this.blocked)
    );

    JPanel addFriendPanel = new JPanel();
    addFriendPanel.setLayout(new BoxLayout(addFriendPanel, BoxLayout.PAGE_AXIS));

    JPanel usernameSearchPanel = new JPanel();
    this.usernameSearchField = new JTextField(20);
    JLabel usernameSearchLabel = new JLabel("Enter their username:");
    usernameSearchLabel.setLabelFor(this.usernameSearchField);
    usernameSearchPanel.add(usernameSearchLabel);
    usernameSearchPanel.add(usernameSearchField);
    
    JPanel requestMessagePanel = new JPanel();
    this.requestMessageField = new JTextField(20);
    JLabel requestMessageLabel = new JLabel("Enter a request message (optional):");
    requestMessageLabel.setLabelFor(this.requestMessageField);
    requestMessagePanel.add(requestMessageLabel);
    requestMessagePanel.add(requestMessageField);
    
    this.sendFriendRequestButton = new JButton("send request");
    this.sendFriendRequestButton.addActionListener(this);

    addFriendPanel.add(usernameSearchPanel);
    addFriendPanel.add(requestMessagePanel);
    addFriendPanel.add(this.sendFriendRequestButton);
    tabbedPane.addTab("Add friend", addFriendPanel);

    this.getContentPane().add(tabbedPane);    
    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.sendFriendRequestButton) {
      String otherUsername = this.usernameSearchField.getText();
      if (otherUsername.length() == 0) {
        JOptionPane.showMessageDialog(
          this,
          "Username entered is empty!",
          "Illegal Input",
          JOptionPane.INFORMATION_MESSAGE
        );
      } else if (otherUsername.equals(GlobalClient.clientData.getUsername())) {
        JOptionPane.showMessageDialog(
          this,
          "Cannot add yourself as a friend!",
          "Illegal Input",
          JOptionPane.INFORMATION_MESSAGE
        );
      } else {
        this.getClientSocket().sendPayload(
          new FriendRequestToServer(
            1,
            GlobalClient.clientData.getUserId(),
            GlobalClient.clientData.getToken(),
            otherUsername,
            this.requestMessageField.getText()
          )
        );
      }
    }
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    this.updateJLists(updatedClientData);
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

    if (
      (payloadType == PayloadType.FRIEND_REQUEST)
      || (payloadType == PayloadType.FRIEND_REQUEST_RESPONSE)
      || (payloadType == PayloadType.BLOCK_USER)
    ) {
      JOptionPane.showMessageDialog(this, notifMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private synchronized void updateJLists(ClientData updatedClientData) {
    // friends and online friends
    DefaultListModel<UserMetadata> friendsListModel = new DefaultListModel<>();
    DefaultListModel<UserMetadata> onlineFriendsListModel = new DefaultListModel<>();
    LinkedHashSet<UserMetadata> friendsMetadata = updatedClientData.getFriends();
    for (UserMetadata curMetadata: friendsMetadata) {
      friendsListModel.addElement(curMetadata);
      if (curMetadata.getStatus() != UserStatus.OFFLINE) {
        onlineFriendsListModel.addElement(curMetadata);
      }
    }
    this.friends = new JList<UserMetadata>(friendsListModel);
    this.onlineFriends = new JList<UserMetadata>(onlineFriendsListModel);

    // incoming friend requests
    DefaultListModel<IncomingFriendRequest> incomingFriendRequestListModel = new DefaultListModel<>();
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequestsMetadata = updatedClientData.getIncomingFriendRequests();
    for (UserMetadata curMetadata: incomingFriendRequestsMetadata.keySet()) {
      incomingFriendRequestListModel.addElement(
        new IncomingFriendRequest(
          curMetadata,
          incomingFriendRequestsMetadata.get(curMetadata)
        )
      );
    }
    this.incomingFriendRequests = new JList<IncomingFriendRequest>(incomingFriendRequestListModel);

    // outgoing friend requests
    DefaultListModel<OutgoingFriendRequest> outgoingFriendRequestListModel = new DefaultListModel<>();
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequestsMetadata = updatedClientData.getOutgoingFriendRequests();
    for (UserMetadata curMetadata: outgoingFriendRequestsMetadata.keySet()) {
      outgoingFriendRequestListModel.addElement(
        new OutgoingFriendRequest(
          curMetadata,
          outgoingFriendRequestsMetadata.get(curMetadata)
        )
      );
    }
    this.outgoingFriendRequests = new JList<OutgoingFriendRequest>(outgoingFriendRequestListModel);
  
    // blocked
    DefaultListModel<UserMetadata> blockedListModel = new DefaultListModel<>();
    LinkedHashSet<UserMetadata> blockedMetadata = updatedClientData.getBlocked();
    for (UserMetadata curMetadata: blockedMetadata) {
      blockedListModel.addElement(curMetadata);
    }
    this.blocked = new JList<UserMetadata>(blockedListModel);

  }

  public class IncomingFriendRequest {
    private UserMetadata senderMetadata;
    private String requestMessage;

    public IncomingFriendRequest(UserMetadata senderMetadata, String requestMessage) {
      this.senderMetadata = senderMetadata;
      this.requestMessage = requestMessage;
    }

    public UserMetadata getSenderMetadata() {
      return this.senderMetadata;
    }

    public String getRequestMessage() {
      return this.requestMessage;
    }
  }

  public class OutgoingFriendRequest {
    private UserMetadata recipientMetadata;
    private String requestMessage;

    public OutgoingFriendRequest(UserMetadata recipientMetadata, String requestMessage) {
      this.recipientMetadata = recipientMetadata;
      this.requestMessage = requestMessage;
    }

    public UserMetadata getRecipientMetadata() {
      return this.recipientMetadata;
    }

    public String getRequestMessage() {
      return this.requestMessage;
    }
  }
}
