package client;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

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
public class LoginFrame extends DisconnectOnCloseFrame implements ActionListener {
  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton registerButton;
  private JLabel statusLabel;
  

  public LoginFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.setSize(StartFrame.WIDTH, StartFrame.HEIGHT);
    this.setResizable(true);

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
    JPanel usernamePanel = new JPanel();
    usernamePanel.add(usernameLabel);
    usernamePanel.add(this.usernameField);
    panel.add(usernamePanel);

    this.passwordField = new JPasswordField(20);
    JLabel passwordLabel = new JLabel("Password: ");
    passwordLabel.setFont(new Font("Serif", Font.PLAIN, 20));
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

      while (true) {
        // user successfully logged in
        if (GlobalClient.hasClientData(this.getClientSocket().getSocket())) {
          this.statusLabel.setForeground(Color.GREEN);
          this.statusLabel.setText("Successfully logged in");
          // load user frame
          MainUserFrame nextFrame = new MainUserFrame(
            this.getTitle(),
            this.getClientSocket()
          );
          this.dispose();
          break;
        }
        // error message
        PriorityBlockingQueue<ErrorMessage> errorMessages = this.getClientSocket().getErrorMessages();
        if (errorMessages.size() > 0) {
          JOptionPane.showMessageDialog(this, errorMessages.poll().getMessage());
          this.statusLabel.setForeground(Color.RED);
          this.statusLabel.setText("Login failed. Please try again");
          break;
        }
      }

    } else if (e.getSource() == this.registerButton) {
      RegistrationFrame nextFrame = new RegistrationFrame(
        this.getTitle(),
        this.getClientSocket()
      );
      this.dispose();
    }
  }
}
