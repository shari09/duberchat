package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import common.entities.Constants;
import client.entities.ClientSocket;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.05.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class ClientStartFrame extends JFrame implements ActionListener {
  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  private static final String DEFAULT_ADDRESS = "127.0.0.1";
  private static final String DEFAULT_PORT = "5000";

  private JTextField addressField;
  private JTextField portField;
  private JLabel errorLabel;

  public ClientStartFrame(String title) {
    super(title);

    try {
      this.setIconImage(ImageIO.read(new File(Constants.ICON_PATH)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(ClientStartFrame.WIDTH, ClientStartFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);
    panel.setBackground(Color.WHITE);
    
    panel.add(Box.createRigidArea(new Dimension(0, 30)));

    // title
    JLabel titleLabel = ClientGUIFactory.getTextLabel(
      "Welcome",
      ClientGUIFactory.getBoldFont(30),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    titleLabel.setAlignmentX(CENTER_ALIGNMENT);
    
    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 50)));

    // fields
    this.addressField = ClientGUIFactory.getTextField(
      20,
      ClientStartFrame.DEFAULT_ADDRESS,
      ClientGUIFactory.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    JLabel addressLabel = ClientGUIFactory.getTextLabel(
      "Server IP address: ",
      ClientGUIFactory.getPlainFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    JPanel addressPanel = new JPanel();
    addressPanel.add(addressLabel);
    addressPanel.add(this.addressField);
    addressPanel.setBackground(Color.WHITE);
    panel.add(addressPanel);

    this.portField = ClientGUIFactory.getTextField(
      20,
      ClientStartFrame.DEFAULT_PORT,
      ClientGUIFactory.getPlainFont(20),
      ClientGUIFactory.GRAY_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_1
    );
    JLabel portLabel = ClientGUIFactory.getTextLabel(
      "Port number: ",
      ClientGUIFactory.getPlainFont(20),
      ClientGUIFactory.PURPLE_SHADE_4
    );
    JPanel portPanel = new JPanel();
    portPanel.add(portLabel);
    portPanel.add(this.portField);
    portPanel.setBackground(Color.WHITE);
    panel.add(portPanel);

    // confirmation button
    JButton button = ClientGUIFactory.getTextButton(
      "Connect",
      ClientGUIFactory.getPlainFont(15),
      ClientGUIFactory.PURPLE_SHADE_4,
      ClientGUIFactory.PURPLE_SHADE_1
    );
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.addActionListener(this);
    panel.add(button);

    // error message
    this.errorLabel = ClientGUIFactory.getTextLabel(
      "",
      ClientGUIFactory.getPlainFont(20),
      ClientGUIFactory.RED_SHADE_1
    );
    this.errorLabel.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(this.errorLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));

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

      LoginFrame nextFrame = new LoginFrame(
        this.getTitle(),
        clientSocket
      );

      this.dispose();
      
    } catch (IOException ioException) {
      this.errorLabel.setText("Failed to connect to server");
    }
  }
}
