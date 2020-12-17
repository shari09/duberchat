package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import client.resources.GlobalJDialogPrompter;
import client.resources.GlobalPayloadQueue;
import common.entities.Constants;
import common.entities.payload.PayloadType;
import common.entities.payload.client_to_server.NewUser;
import common.entities.payload.server_to_client.ServerBroadcast;
import common.gui.Theme;
import common.services.RegexValidator;

/**
 * The frame for the client to register a new account.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 
 * @since 
 */

@SuppressWarnings("serial")
public class RegistrationFrame extends DisconnectOnCloseFrame implements ActionListener {
  public static final int WIDTH = 600;
  public static final int HEIGHT = 800;

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.NEW_USER
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.NEW_USER
  };

  private JTextField usernameField;
  private JTextField descriptionField;
  private JPasswordField passwordField;
  private JPasswordField confirmPasswordField;
  private JButton registerButton;
  private JButton backToLoginButton;
  
  public RegistrationFrame(ClientSocket clientSocket) {
    super(clientSocket);

    this.setSize(RegistrationFrame.WIDTH, RegistrationFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(Color.WHITE);

    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // title
    JLabel titleLabel = ClientGUIFactory.getTextLabel(
      "Create an account",
      Theme.getBoldFont(35),
      ClientGUIFactory.BLUE_SHADE_4
    );
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 3;
    panel.add(titleLabel, constraints);

    constraints.gridy = 2;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);
    
    // fields
    JLabel usernameLabel = ClientGUIFactory.getTextLabel(
      "Username: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_4
    );
    constraints.gridy = 3;
    panel.add(usernameLabel, constraints);
    this.usernameField = ClientGUIFactory.getTextField(
      20,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    
    constraints.gridy = 4;
    panel.add(this.usernameField, constraints);

    constraints.gridy = 5;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);
    
    // description
    JLabel descriptionLabel = ClientGUIFactory.getTextLabel(
      "Enter a line of description of yourself (optional): ",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_4
    );
    constraints.gridy = 6;
    panel.add(descriptionLabel, constraints);
    this.descriptionField = ClientGUIFactory.getTextField(
      20,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.gridy = 7;
    panel.add(this.descriptionField, constraints);

    constraints.gridy = 8;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // password
    JLabel passwordLabel = ClientGUIFactory.getTextLabel(
      "Password: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_4
    );
    constraints.gridy = 9;
    panel.add(passwordLabel, constraints);
    this.passwordField = ClientGUIFactory.getPasswordField(
      20,
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.gridy = 10;
    panel.add(this.passwordField, constraints);

    constraints.gridy = 11;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // confirm password
    JLabel confirmPasswordLabel = ClientGUIFactory.getTextLabel(
      "Confirm password: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.BLUE_SHADE_4
    );
    constraints.gridy = 12;
    panel.add(confirmPasswordLabel, constraints);
    this.confirmPasswordField = ClientGUIFactory.getPasswordField(
      20,
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.gridy = 13;
    panel.add(this.confirmPasswordField, constraints);

    constraints.gridy = 14;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // register account button
    this.registerButton = ClientGUIFactory.getTextButton(
      "Register and Continue",
      Theme.getBoldFont(20),
      ClientGUIFactory.BLUE_SHADE_4,
      ClientGUIFactory.BLUE_SHADE_1
    );
    this.registerButton.addActionListener(this);
    constraints.gridy = 15;
    panel.add(this.registerButton,constraints);

    constraints.gridy = 16;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // button to navigate back to registration page
    this.backToLoginButton = ClientGUIFactory.getTextButton(
      "Back",
      Theme.getBoldFont(20),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1
    );
    this.backToLoginButton.addActionListener(this);
    constraints.gridy = 17;
    panel.add(this.backToLoginButton,constraints);

    constraints.gridy = 18;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);
    constraints.gridx = 4;
    constraints.gridwidth = 1;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return RegistrationFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return RegistrationFrame.ERROR_NOTIF_TYPES;
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.registerButton) {
      String username = this.usernameField.getText();
      String description = this.descriptionField.getText();
      if (description == null) {
        description = "";
      }
      String password = String.valueOf(this.passwordField.getPassword());
      String confirmPassword = String.valueOf(this.confirmPasswordField.getPassword());
      
      if ((username.length() == 0) || (password.length() == 0) || (confirmPassword.length() == 0)) {
        JOptionPane.showMessageDialog(
          this,
          "Please fill in the required fields",
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE,
          ClientGUIFactory.getDialogInformationIcon(30, 30)
        );
        return;
      }

      if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(
          this,
          "Password and confirm password does not match",
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE,
          ClientGUIFactory.getDialogInformationIcon(30, 30)
        );
        return;
      }

      if (
        (!Constants.NAME_VALIDATOR.matches(username))
        || (!Constants.PASSWORD_VALIDATOR.matches(password))
        || (!Constants.DESCRIPTION_VALIDATOR.matches(description))
      ) {
        GlobalJDialogPrompter.warnInvalidInput(
          this,
          "Username, password, or description",
          new RegexValidator[] {
            Constants.NAME_VALIDATOR,
            Constants.PASSWORD_VALIDATOR,
            Constants.DESCRIPTION_VALIDATOR
          }
        );
        return;
      }

      GlobalPayloadQueue.enqueuePayload(
        new NewUser(
          1,
          username,
          password,
          description
        )
      );

    } else if (e.getSource() == this.backToLoginButton) {
      LoginFrame nextFrame = new LoginFrame(this.getClientSocket());
      nextFrame.setLocationRelativeTo(this);
      this.dispose();
    }
  }

  @Override
  public void clientDataUpdated() {
    // user successfully logged in
    if (GlobalClient.hasData()) {
      UserMainFrame nextFrame = new UserMainFrame(this.getClientSocket());
      nextFrame.setLocationRelativeTo(this);
      this.dispose();
    }
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }

}