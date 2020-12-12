package client.gui;

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
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import common.entities.payload.PayloadType;
import common.entities.ClientData;
import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;
import common.entities.payload.Login;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 
 * @since 
 */

@SuppressWarnings("serial")
public class LoginFrame extends DisconnectOnCloseFrame implements ActionListener,
                                                                  ClientSocketListener,
                                                                  MouseMotionListener {
  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton registerButton;
  private JLabel statusLabel;
                              
  public LoginFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.getClientSocket().addListener(this);
    this.addMouseMotionListener(this);

    this.setSize(LoginFrame.WIDTH, LoginFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);

    // title
    JLabel titleLabel = new JLabel("User Login");
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

    this.passwordField = new JPasswordField(20);
    JLabel passwordLabel = new JLabel("Password: ");
    passwordLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    passwordLabel.setLabelFor(this.passwordField);
    JPanel passwordPanel = new JPanel();
    passwordPanel.add(passwordLabel);
    passwordPanel.add(this.passwordField);
    panel.add(passwordPanel);

    // login button
    this.loginButton = new JButton("Login");
    this.loginButton.setAlignmentX(CENTER_ALIGNMENT);
    this.loginButton.addActionListener(this);
    panel.add(this.loginButton);

    // register account button
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    this.registerButton = new JButton("Register for an account");
    this.registerButton.setAlignmentX(CENTER_ALIGNMENT);
    this.registerButton.addActionListener(this);
    panel.add(this.registerButton);

    // status message
    this.statusLabel = new JLabel(" ");
    this.statusLabel.setAlignmentX(CENTER_ALIGNMENT);
    this.statusLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(this.statusLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.loginButton) {
      String username = this.usernameField.getText();
      String password = String.valueOf(this.passwordField.getPassword());

      if ((username.length() == 0) || (password.length() == 0)) {
        this.statusLabel.setForeground(Color.RED);
        this.statusLabel.setText("Please fill in the required fields");
        return;
      }

      this.getClientSocket().sendPayload(
        new Login(
          1,
          username,
          password
        )
      );
      
      this.statusLabel.setForeground(Color.GRAY);
      this.statusLabel.setText("Logging in...");

    } else if (e.getSource() == this.registerButton) {
      RegistrationFrame nextFrame = new RegistrationFrame(
        this.getTitle(),
        this.getClientSocket()
      );
      this.dispose();
    }
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    // user successfully logged in
    if (GlobalClient.hasData()) {
      // load user frame
      MainUserFrame nextFrame = new MainUserFrame(
        this.getTitle(),
        this.getClientSocket()
      );
      this.dispose();
    }
  }

  @Override
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {
    if (payloadType == PayloadType.LOGIN) {
      // error message
      if (!successful) {
        this.statusLabel.setForeground(Color.RED);
        this.statusLabel.setText(notifMessage);
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    this.getClientSocket().updateLastActiveTime();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    this.getClientSocket().updateLastActiveTime();
  }
}
