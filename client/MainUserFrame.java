package client;

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

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class MainUserFrame extends DisconnectOnCloseFrame {

  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  public MainUserFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.setSize(StartFrame.WIDTH, StartFrame.HEIGHT);
    this.setResizable(true);

    this.setVisible(true);
  }
}
