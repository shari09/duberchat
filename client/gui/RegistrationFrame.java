package client.gui;

import java.util.concurrent.PriorityBlockingQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import common.entities.payload.NewUser;
import common.entities.payload.PayloadType;
import common.entities.ClientData;
import common.entities.Constants;
import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 
 * @since 
 */

@SuppressWarnings("serial")
public class RegistrationFrame extends DisconnectOnCloseFrame implements ActionListener {
  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  private JTextField usernameField;
  private JTextField descriptionField;
  private JPasswordField passwordField;
  private JPasswordField confirmPasswordField;
  private JButton registerButton;
  private JButton backToLoginButton;
  
  public RegistrationFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.setSize(RegistrationFrame.WIDTH, RegistrationFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);

    // title
    JLabel titleLabel = new JLabel("Create an Account");
    titleLabel.setAlignmentX(CENTER_ALIGNMENT);
    titleLabel.setFont(new Font("Serif", Font.PLAIN, 30));
    
    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 50)));

    // fields
    this.usernameField = new JTextField(20);
    JLabel usernameLabel = new JLabel("Username: ");
    usernameLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    usernameLabel.setLabelFor(this.usernameField);
    JPanel usernamePanel = new JPanel();
    usernamePanel.add(usernameLabel);
    usernamePanel.add(this.usernameField);
    panel.add(usernamePanel);

    this.descriptionField = new JTextField(20);
    JLabel descriptionLabel = new JLabel("Enter a line of description of yourself (optional): ");
    descriptionLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    descriptionLabel.setLabelFor(this.descriptionField);
    JPanel descriptionPanel = new JPanel();
    descriptionPanel.add(descriptionLabel);
    descriptionPanel.add(this.descriptionField);
    panel.add(descriptionPanel);

    this.passwordField = new JPasswordField(20);
    JLabel passwordLabel = new JLabel("Password: ");
    passwordLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    passwordLabel.setLabelFor(this.passwordField);
    JPanel passwordPanel = new JPanel();
    passwordPanel.add(passwordLabel);
    passwordPanel.add(this.passwordField);
    panel.add(passwordPanel);

    this.confirmPasswordField = new JPasswordField(20);
    JLabel confirmPasswordLabel = new JLabel("Confirm password: ");
    confirmPasswordLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    confirmPasswordLabel.setLabelFor(this.confirmPasswordField);
    JPanel confirmPasswordPanel = new JPanel();
    confirmPasswordPanel.add(confirmPasswordLabel);
    confirmPasswordPanel.add(this.confirmPasswordField);
    panel.add(confirmPasswordPanel);

    // register account button
    this.registerButton = new JButton("Register and Continue");
    this.registerButton.setAlignmentX(CENTER_ALIGNMENT);
    this.registerButton.addActionListener(this);
    panel.add(this.registerButton);

    // button to navigate back to registration page
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    this.backToLoginButton = new JButton("back");
    this.backToLoginButton.setAlignmentX(CENTER_ALIGNMENT);
    this.backToLoginButton.addActionListener(this);
    panel.add(this.backToLoginButton);

    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.registerButton) {
      String username = this.usernameField.getText();
      String description = this.descriptionField.getText();
      String password = String.valueOf(this.passwordField.getPassword());
      String confirmPassword = String.valueOf(this.confirmPasswordField.getPassword());
      
      if ((username.length() == 0) || (password.length() == 0) || (confirmPassword.length() == 0)) {
        JOptionPane.showMessageDialog(
          this,
          "Please fill in the required fields",
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE
        );
        return;
      }

      if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(
          this,
          "Password and confirm password does not match",
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE
        );
        return;
      }

      if (
        (!Constants.NAME_VALIDATOR.matches(username))
        || (!Constants.PASSWORD_VALIDATOR.matches(password))
        || (!Constants.DESCRIPTION_VALIDATOR.matches(description))
      ) {
        JOptionPane.showMessageDialog(
          this,
          "Username, password, or description does not meet requirements:"
          + "\nUsername: " + Constants.NAME_VALIDATOR.getDescription()
          + "\nPassword: " + Constants.PASSWORD_VALIDATOR.getDescription()
          + "\nDescription: " + Constants.DESCRIPTION_VALIDATOR.getDescription(),
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE
        );
        return;
      }

      this.getClientSocket().sendPayload(
        new NewUser(
          1,
          username,
          password,
          description
        )
      );

    } else if (e.getSource() == this.backToLoginButton) {
      this.dispose();
      LoginFrame nextFrame = new LoginFrame(this.getTitle(), this.getClientSocket());
    }
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    // user successfully logged in
    if (GlobalClient.hasData()) {
      this.dispose();
      UserMainFrame nextFrame = new UserMainFrame(
        this.getTitle(),
        this.getClientSocket()
      );
    }
  }

  @Override
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {
    if (successful) {
      return;
    }
    if (payloadType == PayloadType.NEW_USER) {
      JOptionPane.showMessageDialog(
        this,
        notifMessage,
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }
}