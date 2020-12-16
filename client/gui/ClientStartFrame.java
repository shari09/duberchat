package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import common.gui.Theme;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import common.entities.Constants;
import common.gui.Theme;
import client.entities.ClientSocket;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.05.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class ClientStartFrame extends JFrame implements ActionListener {
  public static final int WIDTH = 600;
  public static final int HEIGHT = 800;

  private static final String DEFAULT_ADDRESS = "127.0.0.1";
  private static final String DEFAULT_PORT = "5000";

  private JTextField addressField;
  private JTextField portField;
  private JLabel errorLabel;

  public ClientStartFrame() {
    super(Theme.APPLICATION_NAME);

    this.setIconImage(Theme.getIcon());

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(ClientStartFrame.WIDTH, ClientStartFrame.HEIGHT);
    this.setResizable(true);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // title
    JLabel titleLabel = ClientGUIFactory.getTextLabel(
      "Welcome",
      Theme.getBoldFont(30),
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
    JLabel addressLabel = ClientGUIFactory.getTextLabel(
      "Server IP address: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.gridy = 3;
    panel.add(addressLabel, constraints);
    this.addressField = ClientGUIFactory.getTextField(
      20,
      ClientStartFrame.DEFAULT_ADDRESS,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridy = 4;
    panel.add(this.addressField, constraints);

    constraints.gridy = 5;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    JLabel portLabel = ClientGUIFactory.getTextLabel(
      "Port number: ",
      Theme.getPlainFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridy = 6;
    panel.add(portLabel, constraints);
    this.portField = ClientGUIFactory.getTextField(
      20,
      ClientStartFrame.DEFAULT_PORT,
      Theme.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridy = 7;
    panel.add(this.portField,constraints);

    constraints.gridy = 8;
    panel.add(Box.createRigidArea(new Dimension(1, 1)), constraints);

    // confirmation button
    JButton button = ClientGUIFactory.getTextButton(
      "Connect",
      Theme.getBoldFont(25),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1
    );
    button.addActionListener(this);
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridy = 9;
    panel.add(button,constraints);

    // error message
    this.errorLabel = ClientGUIFactory.getTextLabel(
      " ",
      Theme.getPlainFont(20),
      ClientGUIFactory.RED_SHADE_1
    );
    this.errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridy = 10;
    panel.add(this.errorLabel,constraints);

    constraints.gridx = 4;
    constraints.gridwidth = 1;
    panel.add(Box.createRigidArea(new Dimension(0, 50)), constraints);

    this.getContentPane().add(panel);
    this.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    String addressInput = this.addressField.getText();
    String portInput = this.portField.getText();

    if ((addressInput.length() == 0) || (portInput.length() == 0)) {
      this.errorLabel.setText("Please fill in the required fields");
      return;
    }
    
    if ((!addressInput.matches("^[0-9.]+$")) || (!portInput.matches("^[0-9]{1,5}$"))) {
      this.errorLabel.setText("Invalid address or port input");
      return;
    }

    int portNum = Integer.parseInt(portInput);
    try {
      ClientSocket clientSocket = new ClientSocket(addressInput, portNum);
      Thread clientThread = new Thread(clientSocket);
      clientThread.start();

      LoginFrame nextFrame = new LoginFrame(clientSocket);
      nextFrame.setLocationRelativeTo(this);
      this.dispose();
      
    } catch (IOException ioException) {
      this.errorLabel.setText("Failed to connect to server");
    }
  }
}
