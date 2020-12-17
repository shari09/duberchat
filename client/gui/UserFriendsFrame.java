package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.resources.GlobalPayloadQueue;
import common.entities.ClientData;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.FriendRequest;
import common.entities.payload.server_to_client.ServerBroadcast;
import common.gui.Theme;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
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

  public UserFriendsFrame(ClientSocket clientSocket) {
    super(clientSocket);
    this.setTitle("Friends");

    this.setSize(UserFriendsFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserFriendsFrame.PREFERRED_DIMENSION);
    this.setResizable(true);
    
    this.friends = new JList<UserMetadata>();
    this.friends.setCellRenderer(new UserMetadataRenderer());
    this.friends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.friends.addMouseListener(this);

    this.onlineFriends = new JList<UserMetadata>();
    this.onlineFriends.setCellRenderer(new UserMetadataRenderer());
    this.onlineFriends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.onlineFriends.addMouseListener(this);

    this.incomingFriendRequests = new JList<IncomingFriendRequest>();
    this.incomingFriendRequests.setCellRenderer(new IncomingFriendRequestRenderer());
    this.incomingFriendRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.incomingFriendRequests.addMouseListener(this);

    this.outgoingFriendRequests = new JList<OutgoingFriendRequest>();
    this.outgoingFriendRequests.setCellRenderer(new OutgoingFriendRequestRenderer());
    this.outgoingFriendRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.outgoingFriendRequests.addMouseListener(this);

    this.blocked = new JList<UserMetadata>();
    this.blocked.setCellRenderer(new UserMetadataRenderer());
    this.blocked.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.blocked.addMouseListener(this);

    this.updateJLists();

    this.tabbedPane = ClientGUIFactory.getTabbedPane(Theme.getBoldFont(15));
    this.tabbedPane.addTab(
      "Online",
      ClientGUIFactory.getScrollPane(this.onlineFriends)
    );
    this.tabbedPane.addTab(
      "All",
      ClientGUIFactory.getScrollPane(this.friends)
    );
    this.tabbedPane.addTab(
      "Incoming Friend Requests",
      ClientGUIFactory.getScrollPane(this.incomingFriendRequests)
    );
    this.tabbedPane.addTab(
      "Outgoing Friend Requests",
      ClientGUIFactory.getScrollPane(this.outgoingFriendRequests)
    );
    this.tabbedPane.addTab(
      "Blocked",
      ClientGUIFactory.getScrollPane(this.blocked)
    );

    JPanel addFriendPanel = new JPanel();
    addFriendPanel.setLayout(new BoxLayout(addFriendPanel, BoxLayout.PAGE_AXIS));
    addFriendPanel.setBackground(Color.WHITE);
    JPanel usernameSearchPanel = new JPanel();
    usernameSearchPanel.setBackground(Color.WHITE);
    this.usernameSearchField = ClientGUIFactory.getTextField(
      20,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    JLabel usernameSearchLabel = ClientGUIFactory.getTextLabel(
      "Enter their username:",
      Theme.getPlainFont(20), 
      ClientGUIFactory.BLUE_SHADE_4
    );
    usernameSearchLabel.setOpaque(false);
    usernameSearchPanel.add(usernameSearchLabel);
    usernameSearchPanel.add(usernameSearchField);
    
    JPanel requestMessagePanel = new JPanel();
    requestMessagePanel.setBackground(Color.WHITE);
    this.requestMessageField = ClientGUIFactory.getTextField(
      20,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    JLabel requestMessageLabel = ClientGUIFactory.getTextLabel(
      "Enter a request message (optional):",
      Theme.getPlainFont(20), 
      ClientGUIFactory.BLUE_SHADE_4
    );
    requestMessagePanel.setOpaque(false);
    requestMessagePanel.add(requestMessageLabel);
    requestMessagePanel.add(requestMessageField);
    
    this.sendFriendRequestButton = ClientGUIFactory.getTextButton(
      "send request",
      Theme.getBoldFont(25),
      ClientGUIFactory.BLUE_SHADE_4,
      ClientGUIFactory.BLUE_SHADE_1,
      10,
      10
    );
    this.sendFriendRequestButton.setHorizontalAlignment(JButton.CENTER);
    this.sendFriendRequestButton.setAlignmentX(JButton.CENTER);
    this.sendFriendRequestButton.addActionListener(this);

    addFriendPanel.add(Box.createRigidArea(new Dimension(10, 20)));
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
          JOptionPane.INFORMATION_MESSAGE,
          ClientGUIFactory.getDialogInformationIcon(30, 30)
        );
      } else if (otherUsername.equals(GlobalClient.clientData.getUsername())) {
        JOptionPane.showMessageDialog(
          this,
          "Cannot add yourself as a friend!",
          "Illegal Input",
          JOptionPane.INFORMATION_MESSAGE,
          ClientGUIFactory.getDialogInformationIcon(30, 30)
        );
      } else {
        GlobalPayloadQueue.enqueuePayload(
          new FriendRequest(
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
  public void clientDataUpdated() {
    this.updateJLists();
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
            selected.getRequestMessage()
          );
        }

      } else if (e.getSource() == this.outgoingFriendRequests) {
        OutgoingFriendRequest selected = this.outgoingFriendRequests.getSelectedValue();
        if (selected != null) {
          GlobalJDialogPrompter.promptCancelFriendRequest(
            this,
            selected.getRecipientMetadata()
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

    // friend actions
    } else if (SwingUtilities.isRightMouseButton(e)) {
      if (e.getSource() == this.friends) {
        int row = this.friends.locationToIndex(e.getPoint());
        this.friends.setSelectedIndex(row);
        System.out.println("-----asdasdasd");
        UserMetadata metadata = this.onlineFriends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.promptFriendAction(this, metadata);
        }

      } else if (e.getSource() == this.onlineFriends) {
        int row = this.onlineFriends.locationToIndex(e.getPoint());
        this.onlineFriends.setSelectedIndex(row);
        UserMetadata metadata = this.onlineFriends.getSelectedValue();
        if (metadata != null) {
          GlobalJDialogPrompter.promptFriendAction(this, metadata);
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

  private synchronized void updateJLists() {
    ClientData clientData = GlobalClient.clientData;
    // friends and online friends
    DefaultListModel<UserMetadata> friendsListModel = new DefaultListModel<>();
    DefaultListModel<UserMetadata> onlineFriendsListModel = new DefaultListModel<>();
    LinkedHashSet<UserMetadata> friendsMetadata = clientData.getFriends();
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
    ConcurrentHashMap<UserMetadata, String> incomingFriendRequestsMetadata = clientData.getIncomingFriendRequests();
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
    ConcurrentHashMap<UserMetadata, String> outgoingFriendRequestsMetadata = clientData.getOutgoingFriendRequests();
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
    LinkedHashSet<UserMetadata> blockedMetadata = clientData.getBlocked();
    for (UserMetadata curMetadata: blockedMetadata) {
      System.out.println("blocked: " + curMetadata);
      blockedListModel.addElement(curMetadata);
    }
    this.blocked.setModel(blockedListModel);
    this.blocked.revalidate();

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

  private class IncomingFriendRequestRenderer implements ListCellRenderer<IncomingFriendRequest> {
    @Override
    public Component getListCellRendererComponent(
      JList<? extends IncomingFriendRequest> requests,
      IncomingFriendRequest req,
      int index,
      boolean isSelected,
      boolean hasFocus
    ) {
      JPanel panel = ClientGUIFactory.getIncomingFriendRequestPanel(
        req.getSenderMetadata(),
        req.getRequestMessage(),
        Theme.getBoldFont(15),
        Theme.getPlainFont(15),
        ClientGUIFactory.BLUE_SHADE_3
      );
      return panel;
    }
  }

  private class OutgoingFriendRequestRenderer implements ListCellRenderer<OutgoingFriendRequest> {
    @Override
    public Component getListCellRendererComponent(
      JList<? extends OutgoingFriendRequest> requests,
      OutgoingFriendRequest req,
      int index,
      boolean isSelected,
      boolean hasFocus
    ) {
      JPanel panel = ClientGUIFactory.getOutgoingFriendRequestPanel(
        req.getRecipientMetadata(),
        Theme.getBoldFont(15),
        Theme.getPlainFont(15),
        ClientGUIFactory.BLUE_SHADE_3
      );
      return panel;
    }
  }
}
