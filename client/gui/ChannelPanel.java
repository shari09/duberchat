package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

/**
 * The panel for a channel's display.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class ChannelPanel extends JPanel implements ActionListener,
                                                    MouseListener, 
                                                    AdjustmentListener,
                                                    DocumentListener {

  private static final int MESSAGE_REQUEST_QUANTITY = 50;

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
    this.messagesList.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    this.requestMessages();
    this.syncClientData();

    this.fileChooser = new JFileChooser();
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    this.fileChooser.setMultiSelectionEnabled(false);
    
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
    GridBagConstraints c = ClientGUIFactory.getDefaultGridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weightx = 1;
    c.weighty = 0;
    JLabel title = ClientGUIFactory.getTextLabel(
      ChannelServices.getChannelTitle(this.channelId),
      Theme.getBoldFont(20),
      ClientGUIFactory.BLUE_SHADE_3
    );
    title.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, ClientGUIFactory.GRAY_SHADE_2), 
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    title.setHorizontalAlignment(JLabel.CENTER);
    panel.add(title, c);

    JScrollPane msgScrollPane = ClientGUIFactory.getScrollPane(this.messagesList);
    this.messageScrollBar = msgScrollPane.getVerticalScrollBar();
    this.messageScrollBar.addAdjustmentListener(this);
    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 1;
    panel.add(msgScrollPane, c);

    // text input area
    this.inputArea = ClientGUIFactory.getTextArea(
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_4,
      Color.WHITE
    );
    this.inputArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    this.inputArea.setEditable(true);
    this.inputArea.getDocument().addDocumentListener(this);
    InputMap input = inputArea.getInputMap();
    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
    KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
    input.put(shiftEnter, "insert-break");
    input.put(enter, "message-submit");
    ActionMap actions = this.inputArea.getActionMap();
    actions.put(
      "message-submit",
      new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          sendMessage();
        }
      }
    );

    JScrollPane inputScrollPane = ClientGUIFactory.getScrollPane(this.inputArea);
    inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weightx = 1;
    c.weighty = 0;
    panel.add(inputScrollPane, c);

    // buttons

    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 0;
    c.weighty = 0;
    panel.add(this.getButtons(), c);

    this.add(panel);
    

    // participants
    JPanel partPanel = new JPanel(new BorderLayout());
    partPanel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    partPanel.add(
      ClientGUIFactory.getTextLabel("Participants", Theme.getBoldFont(15), ClientGUIFactory.BLUE_SHADE_4),
      BorderLayout.PAGE_START
    );
    JScrollPane partScrollPane = ClientGUIFactory.getScrollPane(this.participantsList);
    partScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    partPanel.add(partScrollPane, BorderLayout.CENTER);
    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 2;
    c.gridheight = 2;
    c.weightx = 0.75;
    this.add(partPanel, BorderLayout.EAST);
    
    
    this.setVisible(true);
  }


  private JPanel getButtons() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    

    panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    this.uploadAttachmentButton = ClientGUIFactory.getImageButton(
      ClientGUIFactory.getIcon("client/assets/attach.png", 20, 20)
    );
    this.uploadAttachmentButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.uploadAttachmentButton.addActionListener(this);

    this.attachmentLabel = ClientGUIFactory.getTextLabel(
      "",
      Theme.getPlainFont(10),
      ClientGUIFactory.BLUE_SHADE_4
    );
    this.attachmentLabel.setOpaque(false);
    this.attachmentLabel.setHorizontalAlignment(JLabel.CENTER);
    this.attachmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    // send message button
    this.sendMessageButton = ClientGUIFactory.getTextButton(
      "Send",
      Theme.getBoldFont(20),
      ClientGUIFactory.GREEN_SHADE_3,
      ClientGUIFactory.GREEN_SHADE_1
    );
    this.sendMessageButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    this.sendMessageButton.addActionListener(this);



    c.weightx = 0;
    c.weighty = 0;
    c.gridy = 0;

    panel.add(this.uploadAttachmentButton, c);
    panel.add(this.attachmentLabel, c);
    panel.add(this.sendMessageButton, c);
    
    return panel;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.sendMessageButton) {
      this.sendMessage();

    } else if (e.getSource() == this.uploadAttachmentButton) {
      if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = this.fileChooser.getSelectedFile();
        this.attachmentLabel.setText(file.getName());
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
        this.requestMessages();
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

  private void sendMessage() {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();
    
    String text = this.inputArea.getText();
    File file = this.fileChooser.getSelectedFile();
    byte[] attachment = null;
    String attachmentName = null;
    if (file != null) {
      try {
        attachment = Files.readAllBytes(file.toPath());
        attachmentName = file.getName();
      } catch (IOException ioException) {
        JOptionPane.showMessageDialog(
          this,
          "Failed to upload attachment",
          "Error",
          JOptionPane.ERROR_MESSAGE,
          ClientGUIFactory.getDialogErrorIcon(30, 30)
        );
      }
    }

    if ((text.length() > 0) || (attachment != null)) {
      if (Constants.MESSAGE_VALIDATOR.matches(text)) {
        GlobalPayloadQueue.enqueuePayload(
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
        this.attachmentLabel.setText("");
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
        JOptionPane.INFORMATION_MESSAGE,
        ClientGUIFactory.getDialogInformationIcon(30, 30)
      );
    }
  }

  private synchronized void updateJLists() {
    ConcurrentSkipListSet<Message> messages = GlobalClient.messagesData.get(this.channelId);
    if (messages != null) {
      DefaultListModel<Message> messagesListModel = new DefaultListModel<>();
      Iterator<Message> iterator = messages.iterator();
      while (iterator.hasNext()) {
        Message msg = iterator.next();
        messagesListModel.add(0, msg); // most recent to earliest messages from bottom to top
      }
      this.messagesList.setModel(messagesListModel);
      this.messagesList.revalidate();
    }
    LinkedHashSet<UserMetadata> participants = ChannelServices.getChannelByChannelId(this.channelId).getParticipants();
    if (participants != null) {
      DefaultListModel<UserMetadata> participantsListModel = new DefaultListModel<>();
      Iterator<UserMetadata> iterator = participants.iterator();
      while (iterator.hasNext()) {
        UserMetadata participant = iterator.next();
        participantsListModel.addElement(participant);
      }
      this.participantsList.setModel(participantsListModel);
      this.participantsList.revalidate();
    }

    this.messagesList.repaint();
    // if (this.messageScrollBar != null) {
    //   this.messageScrollBar.setValue(this.messageScrollBar.getMaximum());
    // }

  }

  private synchronized void requestMessages() {
    if (GlobalClient.messageHistoryFullyLoaded.get(this.channelId) == null) {
      GlobalClient.messageHistoryFullyLoaded.put(this.channelId, false);
    }
    
    if (GlobalClient.messageHistoryFullyLoaded.get(this.channelId)) {
      return;
    }
    
    Timestamp before = ChannelServices.getEarliestStoredMessageTime(this.channelId);
    if (before == null) {
      before = new Timestamp(System.currentTimeMillis());
    }
    
    GlobalPayloadQueue.enqueuePayload(
      new RequestMessages(
        1,
        GlobalClient.clientData.getUserId(),
        GlobalClient.clientData.getToken(),
        this.channelId,
        before,
        ChannelPanel.MESSAGE_REQUEST_QUANTITY
      )
    );
    System.out.println("------messages requested");
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
      LineBorder border = new LineBorder(ClientGUIFactory.BLUE_SHADE_3, 1);
      panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 15)));
      panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
      return panel;
    }
  }
  
  private class MessageRenderer implements ListCellRenderer<Message> {
    private JList<UserMetadata> participants;

    public MessageRenderer(JList<UserMetadata> participants) {
      this.participants = participants;
      this.participants.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    }

    @Override
    public Component getListCellRendererComponent(
      JList<? extends Message> messages,
      Message msg,
      int index,
      boolean isSelected,
      boolean hasFocus
    ) {
      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints c = ClientGUIFactory.getDefaultGridBagConstraints();
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.WEST;
      panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);


      JLabel icon = new JLabel(ClientGUIFactory.getUserIcon(17, 17));
      

      JLabel username = ClientGUIFactory.getTextLabel(
        this.getUsernameFromId(msg.getSenderId()),
        Theme.getBoldFont(15),
        ClientGUIFactory.PURPLE_SHADE_4
      );


      

      String created = msg.getCreated().toString();
      JLabel time = ClientGUIFactory.getTextLabel(
        "\t" + created.substring(0, created.length()-4),
        Theme.getPlainFont(12),
        ClientGUIFactory.GRAY_SHADE_3
      );

      c.gridx = 0;
      c.gridy = 0;
      c.weighty = 0;
      c.weightx = 0;
      
      panel.add(icon, c);

      c.gridx = 1;
      panel.add(username, c);

      c.gridx = 2;
      panel.add(time, c);

      c.weightx = 1;
      c.gridx = 3;
      panel.add(Box.createHorizontalGlue(), c);

      c.gridy = 1;

      JTextArea content = ClientGUIFactory.getTextArea(
        msg.getContent(),
        Theme.getPlainFont(15),
        ClientGUIFactory.GRAY_SHADE_4,
        ClientGUIFactory.GRAY_SHADE_1
      );
      if (msg.getContent().equals("")) {
        content.setVisible(false);
      }

      
      c.gridx = 1;
      c.weightx = 1;
      c.gridwidth = 3;
      content.setEditable(false);
      content.setBorder(BorderFactory.createEmptyBorder());
      
      c.fill = GridBagConstraints.HORIZONTAL;
      panel.add(content, c);

      if (msg.hasAttachment()) {
        JLabel label = ClientGUIFactory.getTextLabel(
          "Attachment: " + msg.getAttachmentName(),
          Theme.getItalicFont(15),
          ClientGUIFactory.MAGENTA_SHADE_2
        );
        label.setOpaque(false);
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(label, c);
      }
      panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
      return panel;
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
