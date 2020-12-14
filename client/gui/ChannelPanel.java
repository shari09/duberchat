package client.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.services.ChannelServices;
import common.entities.Token;
import common.entities.ChannelMetadata;
import common.entities.Message;
import common.entities.UserMetadata;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.payload.MessageToServer;
import common.entities.payload.RequestMessages;

@SuppressWarnings("serial")
public class ChannelPanel extends JPanel implements ActionListener, MouseListener {
  public static final Dimension SIZE = new Dimension(600, 800);

  private static final int MESSAGE_REQUEST_QUANTITY = 50;
  private static final String DEFAULT_ATTACHMENT_LABEL_TEXT = "no file selected";

  private final String channelId;

  private ClientSocket clientSocket;

  private JTextArea inputArea;
  private JFileChooser fileChooser;
  private JLabel attachmentLabel;
  private JButton uploadAttachmentButton;
  private JButton sendMessageButton;

  private JList<Message> messagesList;
  private JList<UserMetadata> participantsList;
  

  public ChannelPanel(String channelId, ClientSocket clientSocket) {
    super();
    
    this.setSize(ChannelPanel.SIZE);
    this.setLayout(new BorderLayout());

    this.channelId = channelId;
    this.clientSocket = clientSocket;

    this.messagesList = new JList<Message>();
    this.messagesList.addMouseListener(this);
    this.participantsList = new JList<UserMetadata>();
    this.requestMessages();
    this.syncClientData();

    this.fileChooser = new JFileChooser();
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    this.fileChooser.setMultiSelectionEnabled(false);

    JScrollPane msgScrollPane = new JScrollPane(this.messagesList);
    msgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.add(msgScrollPane, BorderLayout.CENTER);

    JPanel partPanel = new JPanel(new BorderLayout());
    partPanel.add(new JLabel("Participants"), BorderLayout.PAGE_START);
    JScrollPane partScrollPane = new JScrollPane(this.participantsList);
    partScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    partScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    partPanel.add(partScrollPane, BorderLayout.CENTER);
    this.add(partPanel, BorderLayout.EAST);

    JPanel inputPanel = new JPanel();
    // text input area
    this.inputArea = new JTextArea(10, 30);
    this.inputArea.setEditable(true);
    inputPanel.add(this.inputArea);
    // attachment upload
    this.uploadAttachmentButton = new JButton("Select an attachment");
    this.uploadAttachmentButton.addActionListener(this);
    inputPanel.add(this.uploadAttachmentButton);
    this.attachmentLabel = new JLabel(ChannelPanel.DEFAULT_ATTACHMENT_LABEL_TEXT);
    inputPanel.add(this.attachmentLabel);
    this.sendMessageButton = new JButton("Send");
    this.sendMessageButton.addActionListener(this);
    inputPanel.add(this.sendMessageButton);
    this.add(inputPanel, BorderLayout.PAGE_END);

    this.setVisible(true);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

    if (e.getSource() == this.sendMessageButton) {
      String text = this.inputArea.getText();

      File file = this.fileChooser.getSelectedFile();
      byte[] attachment = null;
      String attachmentName = null;
      if (file != null) {
        try {
          attachment = Files.readAllBytes(file.toPath());
          attachmentName = file.getName();
        } catch (IOException ioException) {
          JOptionPane.showMessageDialog(this, "Failed to upload attachment", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

      if ((text.length() > 0) || (attachment != null)) {
        this.clientSocket.sendPayload(
          new MessageToServer (
            1,
            userId,
            token,
            this.channelId,
            text,
            attachment,
            attachmentName
          )
        );
        // reset inputs
        this.inputArea.setText("");
        this.attachmentLabel.setText(ChannelPanel.DEFAULT_ATTACHMENT_LABEL_TEXT);
        this.fileChooser.setSelectedFile(null);
      } else {
        JOptionPane.showMessageDialog(
          this,
          "Please enter something or upload an attachment",
          "Invalid Input", 
          JOptionPane.INFORMATION_MESSAGE
        );
      }

    } else if (e.getSource() == this.uploadAttachmentButton) {
      if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = this.fileChooser.getSelectedFile();
        this.attachmentLabel.setText("Chosen file: " + file.getName());
      }
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getSource() == this.messagesList) {
      if (SwingUtilities.isRightMouseButton(e)) {
        int row = this.messagesList.locationToIndex(e.getPoint());
        this.messagesList.setSelectedIndex(row);
        Message msg = this.messagesList.getSelectedValue();
        GlobalJDialogPrompter.promptMessageAction(
          this,
          msg,
          this.clientSocket
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

  public void syncClientData() {
    System.out.println("syncing..");
    this.updateJLists();
    this.revalidate();
    this.repaint();
  }

  public synchronized String getChannelTitle() {
    String title = "";
    ChannelMetadata channelMetadata = ChannelServices.getChannelByChannelId(this.channelId);
    
    if (channelMetadata instanceof PrivateChannelMetadata) {
      PrivateChannelMetadata pcMeta = ((PrivateChannelMetadata)channelMetadata);
      title = ChannelServices.getOtherUserInPrivateChannel(pcMeta).getUsername();

    } else if (channelMetadata instanceof GroupChannelMetadata) {
      title = ((GroupChannelMetadata)channelMetadata).getChannelName();
    }
    return title;
  }

  public String getChannelId() {
    return this.channelId;
  }

  private synchronized void updateJLists() {
    ConcurrentSkipListSet<Message> messages = GlobalClient.messagesData.get(this.channelId);
    if (messages != null) {
      DefaultListModel<Message> messagesListModel = new DefaultListModel<>();
      for (Message msg: messages) {
        messagesListModel.addElement(msg);
      }
      this.messagesList.setModel(messagesListModel);
      this.messagesList.revalidate();
    }
    LinkedHashSet<UserMetadata> participants = ChannelServices.getChannelByChannelId(this.channelId).getParticipants();
    if (participants != null) {
      DefaultListModel<UserMetadata> participantsListModel = new DefaultListModel<>();
      for (UserMetadata participant: participants) {
        participantsListModel.addElement(participant);
      }
      this.participantsList.setModel(participantsListModel);
      this.participantsList.revalidate();
    }
  }

  private void requestMessages() {
    synchronized (GlobalClient.clientData) {
      Timestamp before = ChannelServices.getEarliestStoredMessageTime(this.channelId);
      if (before == null) {
        before = new Timestamp(System.currentTimeMillis());
      }
      
      this.clientSocket.sendPayload(
        new RequestMessages(
          1,
          GlobalClient.clientData.getUserId(),
          GlobalClient.clientData.getToken(),
          this.channelId,
          before,
          ChannelPanel.MESSAGE_REQUEST_QUANTITY
        )
      );
    }
    System.out.println("messages requested");
  }
}
