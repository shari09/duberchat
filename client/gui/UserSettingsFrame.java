package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.JPasswordField;
import javax.swing.JLabel;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import common.entities.Constants;
import common.entities.ProfileField;
import common.entities.Token;
import common.entities.ClientData;
import common.entities.UserStatus;
import common.entities.payload.ChangeProfile;
import common.entities.payload.PayloadType;
import common.entities.payload.ChangePassword;
import common.entities.payload.UpdateStatus;

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

  public UserSettingsFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

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
    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
    // username
    JPanel usernamePanel = new JPanel();
    this.updateUsernameButton = new JButton("Edit");
    this.updateUsernameButton.addActionListener(this);
    JLabel usernameTitle = new JLabel("Username: ");
    this.usernameLabel = new JLabel(username);
    this.usernameLabel.setLabelFor(this.updateUsernameButton);
    usernamePanel.add(usernameTitle);
    usernamePanel.add(this.usernameLabel);
    usernamePanel.add(this.updateUsernameButton);
    contentPane.add(usernamePanel);
    // password
    this.updatePasswordButton = new JButton("Change Password");
    this.updatePasswordButton.addActionListener(this);
    contentPane.add(this.updatePasswordButton);
    // description
    JPanel descriptionPanel = new JPanel(new BorderLayout());
    this.updateDescriptionButton = new JButton("Edit");
    this.updateDescriptionButton.addActionListener(this);
    JLabel descriptionTitle = new JLabel("Description: ");
    this.descriptionLabel = new JLabel(description);
    this.descriptionLabel.setLabelFor(this.updateDescriptionButton);
    descriptionPanel.add(descriptionTitle, BorderLayout.PAGE_START);
    descriptionPanel.add(this.descriptionLabel, BorderLayout.CENTER);
    descriptionPanel.add(this.updateDescriptionButton, BorderLayout.EAST);
    contentPane.add(descriptionPanel);
    // status
    JPanel statusPanel = new JPanel();
    JLabel statusTitle = new JLabel("Status: ");
    this.activeButton = new JRadioButton("active");
    this.activeButton.addActionListener(this);
    this.idleButton = new JRadioButton("idle");
    this.idleButton.addActionListener(this);
    this.offlineButton = new JRadioButton("invisible");
    this.offlineButton.addActionListener(this);
    this.doNotDisturbButton = new JRadioButton("do not disturb");
    this.doNotDisturbButton.addActionListener(this);
    ButtonGroup statusGroup = new ButtonGroup();
    statusGroup.add(this.activeButton);
    statusGroup.add(this.idleButton);
    statusGroup.add(this.offlineButton);
    statusGroup.add(this.doNotDisturbButton);
    statusPanel.add(statusTitle);
    statusPanel.add(this.activeButton);
    statusPanel.add(this.idleButton);
    statusPanel.add(this.offlineButton);
    statusPanel.add(this.doNotDisturbButton);
    contentPane.add(statusPanel);
    this.updateUserStatus(status);
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
      this.promptChangeUsername();

    // password
    } else if (e.getSource() == this.updatePasswordButton) {
      this.promptChangePassword();

    // description
    } else if (e.getSource() == this.updateDescriptionButton) {
      this.promptChangeDescription();

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
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {
    if (
      (payloadType == PayloadType.CHANGE_PROFILE)
      || (payloadType == PayloadType.CHANGE_PASSWORD)
      || (payloadType == PayloadType.UPDATE_STATUS)
    ) {

      if (successful) {
        JOptionPane.showMessageDialog(
          this,
          notifMessage,
          "Success",
          JOptionPane.PLAIN_MESSAGE
        );

      } else {
        JOptionPane.showMessageDialog(
          this,
          notifMessage,
          "Error",
          JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  private void promptChangeUsername() {
    String curUsername = GlobalClient.clientData.getUsername();
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String newUsername = JOptionPane.showInputDialog(
      this,
      "New Username: ",
      curUsername
    );

    if (
      (newUsername == null)
      || (newUsername.length() == 0)
      || (newUsername == curUsername)
    ) {
      return;
    }

    if ((!Constants.NAME_VALIDATOR.matches(newUsername))) {
      JOptionPane.showMessageDialog(
        this,
        "New username does not meet requirements:"
        + "\n" + Constants.NAME_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    this.getClientSocket().sendPayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.USERNAME,
        newUsername
      )
    );
  }

  private synchronized void promptChangePassword() {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    JLabel oldPassLabel = new JLabel("Enter old password:");
    JPasswordField oldPassField = new JPasswordField(20);
    JLabel newPassLabel = new JLabel("Enter new password:");
    JPasswordField newPassField = new JPasswordField(20);
    JLabel confirmPassLabel = new JLabel("Re-type new password:");
    JPasswordField confirmPassField = new JPasswordField(20);
    panel.add(oldPassLabel);
    panel.add(oldPassField);
    panel.add(newPassLabel);
    panel.add(newPassField);
    panel.add(confirmPassLabel);
    panel.add(confirmPassField);
    String[] options = new String[] {"OK", "Cancel"};

    int option = JOptionPane.showOptionDialog(
      this,
      panel,
      "Change Password",
      JOptionPane.NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      null
    );

    if(option != 0) {
      return;
    }

    String oldPassStr = String.valueOf(oldPassField.getPassword());
    String newPassStr = String.valueOf(newPassField.getPassword());
    String confirmNewPassStr = String.valueOf(confirmPassField.getPassword());

    if (
      (oldPassStr == null) || (oldPassStr.length() == 0)
      || (newPassStr == null) || (newPassStr.length() == 0)
      || (confirmNewPassStr== null) || (confirmNewPassStr.length() == 0)
    ) {
      JOptionPane.showMessageDialog(
        this,
        "Required fields incomplete",
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    if ((!Constants.PASSWORD_VALIDATOR.matches(newPassStr))) {
      JOptionPane.showMessageDialog(
        this,
        "New password does not meet requirements:"
        + "\n" + Constants.PASSWORD_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    if (!newPassStr.equals(confirmNewPassStr)) {
      JOptionPane.showMessageDialog(
        this,
        "New password and confirm password does not match",
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    this.getClientSocket().sendPayload(
      new ChangePassword(
        1,
        userId,
        token,
        oldPassStr,
        newPassStr
      )
    );
  }

  private synchronized void promptChangeDescription() {
    String curDescription = GlobalClient.clientData.getDescription();
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String newDescription = JOptionPane.showInputDialog(
      this,
      "New Description: ",
      curDescription
    );

    if (
      (newDescription == null)
      || (newDescription.length() == 0)
      || (newDescription == curDescription)
    ) {
      return;
    }

    if ((!Constants.DESCRIPTION_VALIDATOR.matches(newDescription))) {
      JOptionPane.showMessageDialog(
        this,
        "New description does not meet requirements:"
        + "\n" + Constants.DESCRIPTION_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    this.getClientSocket().sendPayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.DESCRIPTION,
        newDescription
      )
    );
  }


  private synchronized void promptUpdateStatus(JRadioButton radioButton) {
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
      this.getClientSocket().sendPayload(
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
    this.usernameLabel.setText(updatedClientData.getUsername());
    this.usernameLabel.revalidate();
    this.descriptionLabel.setText(updatedClientData.getDescription());
    this.descriptionLabel.revalidate();
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
