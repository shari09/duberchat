package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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

@SuppressWarnings("serial")
public class UserSettingsFrame extends UserFrame implements ActionListener {

  private static final Dimension PREFERRED_DIMENSION = new Dimension(800, 600);

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

  private JLabel usernameLabel;
  private JButton updateUsernameButton;

  private JButton updatePasswordButton;

  private JLabel descriptionLabel;
  private JButton updateDescriptionButton;

  private JRadioButton activeButton;
  private JRadioButton idleButton;
  private JRadioButton offlineButton;
  private JRadioButton doNotDisturbButton;

  public UserSettingsFrame(ClientSocket clientSocket) {
    super(clientSocket);
    this.setTitle("Settings");

    this.setSize(UserSettingsFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserSettingsFrame.PREFERRED_DIMENSION);
    this.setResizable(true);

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
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.fill = GridBagConstraints.NONE;
    constraints.weighty = 1;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;

    // username
    this.usernameLabel = ClientGUIFactory.getTextLabel(
      "Username: " + username,
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.gridy = 0;
    panel.add(this.usernameLabel, constraints);

    this.updateUsernameButton = ClientGUIFactory.getTextButton(
      "Edit",
      Theme.getBoldFont(15),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1,
      10,
      5
    );
    this.updateUsernameButton.addActionListener(this);
    this.updateUsernameButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    constraints.gridx = 1;
    panel.add(this.updateUsernameButton, constraints);
    
    constraints.gridy = 1;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // password
    constraints.gridx = 0;
    constraints.gridy = 2;
    panel.add(
      ClientGUIFactory.getTextLabel(
        "Password",
        Theme.getBoldFont(20),
        ClientGUIFactory.PURPLE_SHADE_4
      ),
      constraints
    );
    this.updatePasswordButton = ClientGUIFactory.getTextButton(
      "Change password",
      Theme.getBoldFont(15),
      ClientGUIFactory.BLUE_SHADE_4,
      ClientGUIFactory.BLUE_SHADE_1,
      10,
      5
    );
    this.updatePasswordButton.addActionListener(this);
    this.updatePasswordButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    constraints.gridx = 1;
    panel.add(this.updatePasswordButton, constraints);

    constraints.gridy = 3;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // description
    this.descriptionLabel = ClientGUIFactory.getTextLabel(
      "Description: " + description,
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.gridx = 0;
    constraints.gridy = 4;
    panel.add(this.descriptionLabel, constraints);

    this.updateDescriptionButton = ClientGUIFactory.getTextButton(
      "Edit",
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1,
      10,
      5
    );
    this.updateDescriptionButton.addActionListener(this);
    this.updateDescriptionButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    constraints.gridx = 1;
    panel.add(this.updateDescriptionButton, constraints);
    
    constraints.gridy = 5;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // status
    JLabel statusTitle = ClientGUIFactory.getTextLabel(
      "Status: ",
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.gridx = 0;
    constraints.gridy = 6;
    panel.add(statusTitle, constraints);

    JPanel statusPanel = new JPanel();
    statusPanel.setBackground(Color.WHITE);
    this.activeButton = ClientGUIFactory.getRadioButton(
      "active",
      Theme.getPlainFont(20),
      ClientGUIFactory.GREEN_SHADE_3,
      5, 20
    );
    this.activeButton.addActionListener(this);
    statusPanel.add(this.activeButton);

    this.idleButton = ClientGUIFactory.getRadioButton(
      "idle",
      Theme.getPlainFont(20),
      ClientGUIFactory.YELLOW_SHADE_2,
      5, 20
    );
    this.idleButton.addActionListener(this);
    statusPanel.add(this.idleButton);
    
    this.offlineButton = ClientGUIFactory.getRadioButton(
      "invisible",
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_3,
      5, 20
    );
    this.offlineButton.addActionListener(this);
    statusPanel.add(this.offlineButton);

    this.doNotDisturbButton = ClientGUIFactory.getRadioButton(
      "do not disturb",
      Theme.getPlainFont(20),
      ClientGUIFactory.RED_SHADE_2,
      5, 10
    );
    this.doNotDisturbButton.addActionListener(this);
    statusPanel.add(this.doNotDisturbButton);

    ButtonGroup statusGroup = new ButtonGroup();
    statusGroup.add(this.activeButton);
    statusGroup.add(this.idleButton);
    statusGroup.add(this.offlineButton);
    statusGroup.add(this.doNotDisturbButton);
    
    statusPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
    constraints.gridx = 1;
    constraints.gridy = 6;
    panel.add(statusPanel, constraints);

    this.add(panel);

    this.updateUserStatus(status);

    this.setVisible(true);
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
  public synchronized void clientDataUpdated(ClientData updatedClientData) {
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
      GlobalPayloadQueue.sendPayload(
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
    this.usernameLabel.setText("Username: " + updatedClientData.getUsername());
    this.usernameLabel.revalidate();
    this.descriptionLabel.setText("Description: " + updatedClientData.getDescription());
    this.descriptionLabel.revalidate();
  }

  private synchronized void updateUserStatus(UserStatus status) {
    System.out.println(status);
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
