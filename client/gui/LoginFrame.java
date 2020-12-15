package client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import common.entities.payload.PayloadType;
import common.entities.ClientData;
import common.gui.Theme;
import client.entities.ClientSocket;
import client.resources.GlobalClient;
import common.entities.payload.client_to_server.Login;
import common.entities.payload.server_to_client.ServerBroadcast;

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
  public static final int WIDTH = 600;
  public static final int HEIGHT = 800;

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.LOGIN
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.LOGIN
  };

  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton registerButton;
                              
  public LoginFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.setSize(LoginFrame.WIDTH, LoginFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(Color.WHITE);

    GridBagConstraints constraints = ClientGUIFactory.getGridBagConstraints(
      0, 0, 1, 1,
      0.5, 0.5,
      10, 10, 
      GridBagConstraints.BOTH,
      GridBagConstraints.CENTER
    );
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // title
    JLabel titleLabel = ClientGUIFactory.getTextLabel(
      "User Login",
      Theme.getBoldFont(35),
      ClientGUIFactory.PURPLE_SHADE_4
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
      ClientGUIFactory.PURPLE_SHADE_4
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

    JLabel passwordLabel = ClientGUIFactory.getTextLabel(
      "Password: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.gridy = 6;
    panel.add(passwordLabel, constraints);
    this.passwordField = ClientGUIFactory.getPasswordField(
      20,
      Theme.getPlainFont(15),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.gridy = 7;
    panel.add(this.passwordField,constraints);

    constraints.gridy = 8;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // login button
    this.loginButton = ClientGUIFactory.getTextButton(
      "Login",
      Theme.getBoldFont(25),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1
    );
    this.loginButton.addActionListener(this);
    constraints.gridy = 9;
    panel.add(this.loginButton,constraints);

    constraints.gridy = 10;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // register account button
    this.registerButton = ClientGUIFactory.getTextButton(
      "Register for an account",
      Theme.getBoldFont(20),
      ClientGUIFactory.BLUE_SHADE_3,
      ClientGUIFactory.BLUE_SHADE_1
    );
    this.registerButton.addActionListener(this);
    constraints.gridy = 11;
    panel.add(this.registerButton,constraints);

    constraints.gridy = 12;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);
    constraints.gridx = 4;
    constraints.gridwidth = 1;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);
    
    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return LoginFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return LoginFrame.ERROR_NOTIF_TYPES;
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.loginButton) {
      String username = this.usernameField.getText();
      String password = String.valueOf(this.passwordField.getPassword());

      if ((username.length() == 0) || (password.length() == 0)) {
        JOptionPane.showMessageDialog(
          this,
          "Please fill in the required fields",
          "Submission failed",
          JOptionPane.INFORMATION_MESSAGE
        );
        return;
      }

      this.getClientSocket().sendPayload(
        new Login(
          1,
          username,
          password
        )
      );

    } else if (e.getSource() == this.registerButton) {
      this.dispose();
      RegistrationFrame nextFrame = new RegistrationFrame(
        this.getTitle(),
        this.getClientSocket()
      );
    }
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    // user successfully logged in
    if (GlobalClient.hasData()) {
      UserMainFrame nextFrame = new UserMainFrame(
        this.getTitle(),
        this.getClientSocket()
      );
    } else {
      System.out.println("failed to initialize client data");
    }
    this.dispose();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }
}
