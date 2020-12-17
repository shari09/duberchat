package client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.resources.GlobalPayloadQueue;
import common.entities.ClientData;
import common.entities.Token;
import common.entities.UserStatus;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.UpdateStatus;
import common.entities.payload.server_to_client.ServerBroadcast;
import common.gui.Theme;

/**
 * The frame for the user to change their settings,
 * including username, password, description and status.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserSettingsFrame extends UserFrame implements ActionListener {

  private static final Dimension PREFERRED_DIMENSION = new Dimension(450, 500);

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.CHANGE_PROFILE,
    PayloadType.CHANGE_PASSWORD,
    PayloadType.UPDATE_STATUS
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.CHANGE_PROFILE,
    PayloadType.CHANGE_PASSWORD,
    PayloadType.UPDATE_STATUS
  };


  private JTextArea usernameText;
  private JButton updateUsernameButton;

  private JButton updatePasswordButton;

  private JTextArea descriptionText;
  private JButton updateDescriptionButton;

  private JRadioButton activeButton;
  private JRadioButton idleButton;
  private JRadioButton offlineButton;
  private JRadioButton doNotDisturbButton;

  public UserSettingsFrame(ClientSocket clientSocket) {
    super(clientSocket);

    this.setVisible(false);
    this.setTitle("Settings");

    this.setSize(UserSettingsFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserSettingsFrame.PREFERRED_DIMENSION);
    
    String username;
    String description;
    UserStatus status;
    synchronized (GlobalClient.clientData) {
      username = GlobalClient.clientData.getUsername();
      description = GlobalClient.clientData.getDescription();
      status = GlobalClient.clientData.getStatus();
    }
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    GridBagConstraints c = ClientGUIFactory.getDefaultGridBagConstraints();


    this.usernameText = new JTextArea(username);
    this.updateUsernameButton = new JButton("Edit");
    this.updateUsernameButton.addActionListener(this);

    this.updatePasswordButton = new JButton("Edit");
    this.updatePasswordButton.addActionListener(this);

    this.descriptionText = new JTextArea(description);
    this.updateDescriptionButton = new JButton("Edit");
    this.updateDescriptionButton.addActionListener(this);


    JPanel usernamePanel = this.getSection(
      "Username", 
      this.usernameText, 
      this.updateUsernameButton
    );


    JPanel passwordPanel = this.getSection(
      "Password", 
      new JTextArea("[Hidden]"), 
      this.updatePasswordButton
    );

    JPanel descriptionPanel = this.getSection(
      "Description", 
      this.descriptionText, 
      this.updateDescriptionButton
    );

    JLabel title = new JLabel("MY ACCOUNT");
    title.setFont(Theme.getBoldFont(16));
    title.setForeground(ClientGUIFactory.PURPLE_SHADE_4);
    title.setOpaque(false);

    panel.add(title, c);


    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 0;


    panel.add(usernamePanel, c);
    c.gridy = 2;
    panel.add(passwordPanel, c);
    c.gridy = 3;
    panel.add(descriptionPanel, c);

    c.gridy = 4;
    c.weighty = 1;
    panel.add(Box.createVerticalGlue(), c);

    c.gridy = 5;
    c.weighty = 0;

    panel.add(this.getStatusPanel(), c);

    this.add(panel);

    this.updateUserStatus(status);

    this.setVisible(true);
  }

  private JPanel getSection(String labelText, JTextArea content, JButton button) {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    GridBagConstraints c = new GridBagConstraints();

    //label
    JLabel label = new JLabel(labelText);
    label.setFont(Theme.getPlainFont(16));
    label.setForeground(ClientGUIFactory.GRAY_SHADE_3);
    label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    label.setOpaque(false);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0;

    panel.add(label, c);

    //content
    content.setOpaque(false);
    content.setFont(Theme.getPlainFont(17));
    content.setForeground(ClientGUIFactory.GRAY_SHADE_4);
    content.setLineWrap(true);
    content.setEditable(false);
    content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

    c.gridy = 1;

    panel.add(content, c);
    //button

    button.setBackground(ClientGUIFactory.PURPLE_SHADE_3);
    button.setForeground(Color.WHITE);
    button.setFont(Theme.getPlainFont(20));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(5, 40, 5, 40));

    c.weightx = 1;
    panel.add(Box.createHorizontalGlue(), c);

    c.gridx = 1;
    c.gridy = 0;
    c.gridheight = 2;
    c.anchor = GridBagConstraints.CENTER;
    panel.add(button, c);
    return panel;
  }

  public JPanel getStatusPanel() {
    JPanel statusPanel = new JPanel();
    statusPanel.setOpaque(false);
    statusPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();


    this.activeButton = ClientGUIFactory.getRadioButton(
      "Active",
      Theme.getPlainFont(15),
      ClientGUIFactory.GREEN_SHADE_3,
      5, 20
    );
    this.activeButton.addActionListener(this);
    
    this.idleButton = ClientGUIFactory.getRadioButton(
      "Idle",
      Theme.getPlainFont(15),
      ClientGUIFactory.YELLOW_SHADE_2,
      5, 20
    );
    this.idleButton.addActionListener(this);
    
    this.offlineButton = ClientGUIFactory.getRadioButton(
      "Invisible",
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_3,
      5, 20
    );
    this.offlineButton.addActionListener(this);
    
    this.doNotDisturbButton = ClientGUIFactory.getRadioButton(
      "Do not disturb",
      Theme.getPlainFont(15),
      ClientGUIFactory.RED_SHADE_2,
      5, 10
    );
    this.doNotDisturbButton.addActionListener(this);

    c.weightx = 0;
    c.weighty = 0;

    c.gridx = 0;
    c.gridy = 0;
    statusPanel.add(this.activeButton, c);

    c.weightx = 1;
    c.gridx = 1;
    statusPanel.add(this.idleButton, c);

    c.gridx = 2;
    statusPanel.add(this.offlineButton, c);

    c.gridx = 3;
    c.weightx = 0;
    statusPanel.add(this.doNotDisturbButton, c);

    ButtonGroup statusGroup = new ButtonGroup();
    statusGroup.add(this.activeButton);
    statusGroup.add(this.idleButton);
    statusGroup.add(this.offlineButton);
    statusGroup.add(this.doNotDisturbButton);

    return statusPanel;
    
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return UserSettingsFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return UserSettingsFrame.ERROR_NOTIF_TYPES;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // username
    if (e.getSource() == this.updateUsernameButton) {
      GlobalJDialogPrompter.promptChangeUsername(this);

    // password
    } else if (e.getSource() == this.updatePasswordButton) {
      GlobalJDialogPrompter.promptChangePassword(this);

    // description
    } else if (e.getSource() == this.updateDescriptionButton) {
      GlobalJDialogPrompter.promptChangeProfileDescription(this);

    // status
    } else if (e.getSource() instanceof JRadioButton) {
      this.promptUpdateStatus((JRadioButton)(e.getSource()));
    }
  }

  @Override
  public synchronized void clientDataUpdated() {
    ClientData updatedClientData = GlobalClient.clientData;
    this.updateLabels(updatedClientData);
    this.updateUserStatus(updatedClientData.getStatus());
    this.repaint();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }

  public synchronized void promptUpdateStatus(JRadioButton radioButton) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();
    UserStatus currentStatus = GlobalClient.clientData.getStatus();

    UserStatus statusToSend = currentStatus;
    if (radioButton == this.activeButton) {
      statusToSend = UserStatus.ACTIVE;
    } else if (radioButton == this.idleButton) {
      statusToSend = UserStatus.IDLE;
    } else if (radioButton == this.offlineButton) {
      statusToSend = UserStatus.OFFLINE;
    } else if (radioButton == this.doNotDisturbButton) {
      statusToSend = UserStatus.DO_NOT_DISTURB;
    }
    if (statusToSend != currentStatus) {
      GlobalPayloadQueue.enqueuePayload(
        new UpdateStatus(
          1,
          userId,
          token,
          statusToSend
        )
      );
    }
  }

  private synchronized void updateLabels(ClientData updatedClientData) {
    this.usernameText.setText(updatedClientData.getUsername());
    this.usernameText.revalidate();
    this.descriptionText.setText(updatedClientData.getDescription());
    this.descriptionText.revalidate();
  }

  private synchronized void updateUserStatus(UserStatus status) {
    switch (status) {
      case ACTIVE:
        this.activeButton.setSelected(true);
        break;

      case IDLE:
        this.idleButton.setSelected(true);
        break;

      case OFFLINE:
        this.offlineButton.setSelected(true);
        break;

      case DO_NOT_DISTURB:
        this.doNotDisturbButton.setSelected(true);
        break;
    }
  }
}
