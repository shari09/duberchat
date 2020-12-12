package client.gui;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

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

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(ClientStartFrame.WIDTH, ClientStartFrame.HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);

    // title
    JLabel titleLabel = new JLabel("Welcome");
    titleLabel.setAlignmentX(CENTER_ALIGNMENT);
    titleLabel.setFont(new Font("Serif", Font.PLAIN, 30));
    
    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 50)));

    // fields
    this.addressField = new JTextField(ClientStartFrame.DEFAULT_ADDRESS, 20);
    JLabel addressLabel = new JLabel("Server IP address: ");
    addressLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    addressLabel.setLabelFor(this.addressField);
    JPanel addressPanel = new JPanel();
    addressPanel.add(addressLabel);
    addressPanel.add(this.addressField);
    panel.add(addressPanel);

    this.portField = new JTextField(ClientStartFrame.DEFAULT_PORT, 20);
    JLabel portLabel = new JLabel("Port number: ");
    portLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    portLabel.setLabelFor(this.portField);
    JPanel portPanel = new JPanel();
    portPanel.add(portLabel);
    portPanel.add(this.portField);
    panel.add(portPanel);

    // confirmation button
    JButton button = new JButton("Connect");
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.addActionListener(this);
    panel.add(button);

    // error message
    this.errorLabel = new JLabel(" ");
    this.errorLabel.setAlignmentX(CENTER_ALIGNMENT);
    this.errorLabel.setFont(new Font("Serif", Font.PLAIN, 20));
    this.errorLabel.setForeground(Color.RED);
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
