package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.resources.GlobalPayloadQueue;
import client.services.ChannelServices;
import client.services.ClientSocketServices;
import common.entities.ChannelMetadata;
import common.entities.Constants;
import common.entities.Message;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.payload.client_to_server.MessageToServer;
import common.entities.payload.client_to_server.RequestMessages;
import common.gui.Theme;

@SuppressWarnings("serial")
public class ChannelPanel extends JPanel implements ActionListener,
                                                    MouseListener, 
                                                    AdjustmentListener,
                                                    DocumentListener {

  private static final int MESSAGE_REQUEST_QUANTITY = 50;
  private static final String DEFAULT_ATTACHMENT_LABEL_TEXT = "no file selected";

  private static final float SCROLL_THRESHOLD = 0.95f;

  private final String channelId;

  private JTextArea inputArea;
  private JFileChooser fileChooser;
  private JLabel attachmentLabel;
  private JButton uploadAttachmentButton;
  private JButton sendMessageButton;
  private JScrollBar messageScrollBar;

  private JList<Message> messagesList;
  private JList<UserMetadata> participantsList;
  

  public ChannelPanel(String channelId, ClientSocket clientSocket) {
    super();
    
    this.setLayout(new BorderLayout());
    this.channelId = channelId;

    this.participantsList = new JList<UserMetadata>();
    this.participantsList.setCellRenderer(
      new ParticipantRenderer(
        ChannelServices.getChannelByChannelId(channelId)
      )
    );
    this.messagesList = new JList<Message>();
    this.messagesList.addMouseListener(this);
    this.messagesList.setCellRenderer(new MessageRenderer(this.participantsList));
    this.requestMessages();
    this.syncClientData();

    this.fileChooser = new JFileChooser();
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    this.fileChooser.setMultiSelectionEnabled(false);
    
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(10, 10, 10, 10);
    constraints.gridwidth = 2;
    constraints.weightx = 0.75;
    constraints.weighty = 0.1;
    JLabel title = ClientGUIFactory.getTextLabel(
      ChannelServices.getChannelTitle(this.channelId),
      Theme.getBoldFont(20),
      ClientGUIFactory.BLUE_SHADE_3
    );
    title.setHorizontalAlignment(JLabel.CENTER);
    panel.add(title, constraints);

    JScrollPane msgScrollPane = ClientGUIFactory.getScrollPane(this.messagesList, true);
    msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    msgScrollPane.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    this.messageScrollBar = msgScrollPane.getVerticalScrollBar();
    this.messageScrollBar.addAdjustmentListener(this);
    constraints.gridy = 1;
    constraints.weightx = 0;
    constraints.weighty = 1;
    panel.add(msgScrollPane, constraints);

    // text input area
    this.inputArea = ClientGUIFactory.getTextArea(
      5,
      30,
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_4,
      Color.WHITE
    );
    this.inputArea.setEditable(true);
    this.inputArea.getDocument().addDocumentListener(this);
    JScrollPane inputScrollPane = ClientGUIFactory.getScrollPane(this.inputArea, true);
    inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 1;
    constraints.weighty = 0.25;
    panel.add(inputScrollPane, constraints);

    // attachment upload
    JPanel buttonsPanel = new JPanel(new BorderLayout());
    buttonsPanel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    this.uploadAttachmentButton = ClientGUIFactory.getTextButton(
      "Select an attachment",
      Theme.getBoldFont(15),
      ClientGUIFactory.BLUE_SHADE_4,
      ClientGUIFactory.BLUE_SHADE_1
    );
    this.uploadAttachmentButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.uploadAttachmentButton.addActionListener(this);
    buttonsPanel.add(this.uploadAttachmentButton, BorderLayout.CENTER);
    this.attachmentLabel = ClientGUIFactory.getTextLabel(
      DEFAULT_ATTACHMENT_LABEL_TEXT,
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_4
    );
    this.attachmentLabel.setOpaque(false);
    this.attachmentLabel.setHorizontalAlignment(JLabel.CENTER);
    buttonsPanel.add(this.attachmentLabel, BorderLayout.NORTH);
    // send message button
    this.sendMessageButton = ClientGUIFactory.getTextButton(
      "Send",
      Theme.getBoldFont(20),
      ClientGUIFactory.GREEN_SHADE_3,
      ClientGUIFactory.GREEN_SHADE_1
    );
    this.sendMessageButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    this.sendMessageButton.addActionListener(this);
    buttonsPanel.add(this.sendMessageButton, BorderLayout.SOUTH);
    constraints.gridx = 1;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 0;
    constraints.fill = GridBagConstraints.NONE;
    constraints.anchor = GridBagConstraints.LAST_LINE_START;
    panel.add(buttonsPanel, constraints);
    this.add(panel, BorderLayout.CENTER);

    // participants
    JPanel partPanel = new JPanel(new BorderLayout());
    partPanel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    partPanel.add(
      ClientGUIFactory.getTextLabel("Participants", Theme.getBoldFont(15), ClientGUIFactory.BLUE_SHADE_4),
      BorderLayout.PAGE_START
    );
    JScrollPane partScrollPane = ClientGUIFactory.getScrollPane(this.participantsList, true);
    partScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    partPanel.add(partScrollPane, BorderLayout.CENTER);
    constraints.gridx = 2;
    constraints.gridy = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.weightx = 0.75;
    this.add(partPanel, BorderLayout.EAST);
    
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
        if (Constants.MESSAGE_VALIDATOR.matches(text)) {
          GlobalPayloadQueue.sendPayload(
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
          GlobalJDialogPrompter.warnInvalidInput(
            this,
            "message",
            Constants.MESSAGE_VALIDATOR
          );
        }

        
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
        GlobalJDialogPrompter.promptMessageAction(this, msg);
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

  @Override
  public void adjustmentValueChanged(AdjustmentEvent e) {
    if (e.getSource() == this.messageScrollBar) {
      int maxValue = this.messageScrollBar.getMaximum() - this.messageScrollBar.getVisibleAmount();
      int curValue = this.messageScrollBar.getValue();
      double fraction = 1 - curValue/1.0/maxValue;
      if (fraction > SCROLL_THRESHOLD) {
        GlobalPayloadQueue.queue.add(
          new RequestMessages(
            1,
            GlobalClient.clientData.getUserId(),
            GlobalClient.clientData.getToken(),
            this.channelId,
            ChannelServices.getEarliestStoredMessageTime(this.channelId),
            MESSAGE_REQUEST_QUANTITY
          )
        );
      }
    }
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    ClientSocketServices.updateLastActiveTime();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    ClientSocketServices.updateLastActiveTime();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    ClientSocketServices.updateLastActiveTime();
  }

  public void syncClientData() {
    this.updateJLists();
    this.revalidate();
    this.repaint();
  }

  public JList<Message> getMessagesList() {
    return this.messagesList;
  }

  public String getChannelId() {
    return this.channelId;
  }

  private synchronized void updateJLists() {
    ConcurrentSkipListSet<Message> messages = GlobalClient.messagesData.get(this.channelId);
    if (messages != null) {
      DefaultListModel<Message> messagesListModel = new DefaultListModel<>();
      for (Message msg: messages) {
        messagesListModel.add(0, msg); // most recent to earliest messages from bottom to top
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
      
      GlobalPayloadQueue.sendPayload(
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


  private class ParticipantRenderer implements ListCellRenderer<UserMetadata> {
    private final ChannelMetadata channelMetadata;

    public ParticipantRenderer(ChannelMetadata channelMetadata) {
      this.channelMetadata = channelMetadata;
    }

    @Override
    public Component getListCellRendererComponent(
      JList<? extends UserMetadata> participants,
      UserMetadata metadata,
      int index,
      boolean isSelected,
      boolean hasFocus
    ) {
      JPanel panel = ClientGUIFactory.getParticipantThumbnailPanel(
        channelMetadata,
        metadata,
        Theme.getBoldFont(15),
        ClientGUIFactory.BLUE_SHADE_3
      );
      LineBorder border = new LineBorder(ClientGUIFactory.BLUE_SHADE_2, 1);
      panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 15)));
      panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
      return panel;
    }
  }
  
  private class MessageRenderer implements ListCellRenderer<Message> {
    private JList<UserMetadata> participants;

    public MessageRenderer(JList<UserMetadata> participants) {
      this.participants = participants;
    }

    @Override
    public Component getListCellRendererComponent(
      JList<? extends Message> messages,
      Message msg,
      int index,
      boolean isSelected,
      boolean hasFocus
    ) {
      JPanel panelToReturn = new JPanel(new GridBagLayout());
      panelToReturn.add(Box.createHorizontalGlue());
      GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      constraints.insets = new Insets(5, 5, 5, 5);
      panelToReturn.setAlignmentX(JPanel.LEFT_ALIGNMENT);
      panelToReturn.setBackground(ClientGUIFactory.GRAY_SHADE_1);

      JPanel senderPanel = new JPanel();
      senderPanel.add(Box.createHorizontalGlue());
      senderPanel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
      senderPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
      senderPanel.add(new JLabel(new ImageIcon(ClientGUIFactory.getUserIcon())));
      senderPanel.add(
        ClientGUIFactory.getTextLabel(
        this.getUsernameFromId(msg.getSenderId()),
        Theme.getBoldFont(15),
        ClientGUIFactory.PURPLE_SHADE_4
        )
      );
      senderPanel.add(
        ClientGUIFactory.getTextLabel(
          "\t" + msg.getCreated().toString(),
        Theme.getPlainFont(15),
        ClientGUIFactory.GRAY_SHADE_3
        )
      );
      constraints.weightx = 0.5;
      constraints.weighty = 0;
      
      panelToReturn.add(senderPanel, constraints);

      JTextArea content = ClientGUIFactory.getTextArea(
        5, 20,
        msg.getContent(),
        Theme.getPlainFont(15),
        ClientGUIFactory.GRAY_SHADE_4,
        Color.WHITE
      );
      constraints.gridy = 1;
      constraints.weighty = 0.5;
      content.setEditable(false);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      panelToReturn.add(content, constraints);

      if (msg.hasAttachment()) {
        JLabel label = ClientGUIFactory.getTextLabel(
          "Attachment: " + msg.getAttachmentName(),
          Theme.getItalicFont(20),
          ClientGUIFactory.MAGENTA_SHADE_2
        );
        label.setBackground(Color.WHITE);
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;
        panelToReturn.add(label, BorderLayout.SOUTH);
      }
      panelToReturn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
      return panelToReturn;
    }

    private String getUsernameFromId(String userId) {
      for (int i = 0; i < this.participants.getModel().getSize(); i++) {
        UserMetadata user = this.participants.getModel().getElementAt(i);
        if (user.getUserId().equals(userId)) {
          return user.getUsername();
        }
      }
      return "[unknown user]";
    }

  }
}
