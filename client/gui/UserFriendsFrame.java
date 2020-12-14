package client.gui;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;

import client.resources.GlobalJDialogPrompter;
import common.entities.ClientData;
import common.entities.payload.ServerBroadcast;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.entities.payload.FriendRequestToServer;
import common.entities.payload.PayloadType;

import client.entities.ClientSocket;
import client.resources.GlobalClient;

@SuppressWarnings("serial")
public class UserFriendsFrame extends UserFrame implements ActionListener, MouseListener {
  private static final Dimension PREFERRED_DIMENSION = new Dimension(800, 600);

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.FRIEND_REQUEST,
    PayloadType.FRIEND_REQUEST_RESPONSE,
    PayloadType.BLOCK_USER
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.FRIEND_REQUEST,
    PayloadType.FRIEND_REQUEST_RESPONSE,
    PayloadType.BLOCK_USER
  };

  private JTabbedPane tabbedPane;
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
    this.setResizable(true);
    
    this.friends = new JList<UserMetadata>();
    this.friends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.friends.addMouseListener(this);

    this.onlineFriends = new JList<UserMetadata>();
    this.onlineFriends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.onlineFriends.addMouseListener(this);

    this.incomingFriendRequests = new JList<IncomingFriendRequest>();
    this.incomingFriendRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.incomingFriendRequests.addMouseListener(this);

    this.outgoingFriendRequests = new JList<OutgoingFriendRequest>();
    this.outgoingFriendRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.outgoingFriendRequests.addMouseListener(this);

    this.blocked = new JList<UserMetadata>();
    this.blocked.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.blocked.addMouseListener(this);

    this.updateJLists(GlobalClient.clientData);

    this.tabbedPane = new JTabbedPane();
    this.tabbedPane.addTab(
      "Online",
      new JScrollPane(this.onlineFriends)
    );
    this.tabbedPane.addTab(
      "All",
      new JScrollPane(this.friends)
    );
    this.tabbedPane.addTab(
      "Incoming Friend Requests",
      new JScrollPane(this.incomingFriendRequests)
    );
    this.tabbedPane.addTab(
      "Outgoing Friend Requests",
      new JScrollPane(this.outgoingFriendRequests)
    );
    this.tabbedPane.addTab(
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
    this.tabbedPane.addTab("Add friend", addFriendPanel);

    this.getContentPane().add(this.tabbedPane);
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return UserFriendsFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return UserFriendsFrame.ERROR_NOTIF_TYPES;
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
    this.repaint();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      if (e.getSource() == this.incomingFriendRequests) {
        IncomingFriendRequest selected = this.incomingFriendRequests.getSelectedValue();
        if (selected != null) {
          GlobalJDialogPrompter.promptRespondFriendRequest(
            this,
            selected.getSenderMetadata(),
            selected.getRequestMessage(),
            this.getClientSocket()
          );
        }

      } else if (e.getSource() == this.outgoingFriendRequests) {
        OutgoingFriendRequest selected = this.outgoingFriendRequests.getSelectedValue();
        if (selected != null) {
          GlobalJDialogPrompter.promptCancelFriendRequest(
            this,
            selected.getRecipientMetadata(),
            this.getClientSocket()
          );
        }

      } else if (e.getSource() == this.friends) {
        UserMetadata metadata = this.friends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.displayUserMetadata(this, metadata);
        }

      } else if (e.getSource() == this.onlineFriends) {
        UserMetadata metadata = this.onlineFriends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.displayUserMetadata(this, metadata);
        }
      }

    } else if (SwingUtilities.isRightMouseButton(e)) {
      if (e.getSource() == this.friends) {
        int row = this.friends.locationToIndex(e.getPoint());
        this.friends.setSelectedIndex(row);
        UserMetadata metadata = this.onlineFriends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.displayUserMetadata(this, metadata);
        }

      } else if (e.getSource() == this.onlineFriends) {
        int row = this.onlineFriends.locationToIndex(e.getPoint());
        this.onlineFriends.setSelectedIndex(row);
        UserMetadata metadata = this.onlineFriends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.displayUserMetadata(this, metadata);
        }
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
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
    this.friends.setModel(friendsListModel);
    this.friends.revalidate();
    this.onlineFriends.setModel(onlineFriendsListModel);
    this.onlineFriends.revalidate();

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
    this.incomingFriendRequests.setModel(incomingFriendRequestListModel);
    this.incomingFriendRequests.revalidate();

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
    this.outgoingFriendRequests.setModel(outgoingFriendRequestListModel);
    this.outgoingFriendRequests.revalidate();

    // blocked
    DefaultListModel<UserMetadata> blockedListModel = new DefaultListModel<>();
    LinkedHashSet<UserMetadata> blockedMetadata = updatedClientData.getBlocked();
    for (UserMetadata curMetadata: blockedMetadata) {
      blockedListModel.addElement(curMetadata);
    }
    this.blocked.setModel(blockedListModel);
    this.blocked.revalidate();

    System.out.println("friends data updated");
  }

  private class IncomingFriendRequest {
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

  private class OutgoingFriendRequest {
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
