package server.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import common.entities.Constants;
import common.gui.Theme;
import server.Server;

/**
 * The start up server window.
 * <p>
 * Created on 2020.12.10.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class StartFrame extends JFrame implements ActionListener {
  private static final int INITIAL_WIDTH = 340;
  private static final int INITIAL_HEIGHT = 410;
  private static final String DEFAULT_PORT = "5000";
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private JTextField portField;
  private JLabel error;
  private Server server;

  public StartFrame(Server socket) {
    super(Theme.APPLICATION_NAME + " Server");
    this.setIconImage(Theme.getIcon());
    
    this.server = socket;
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(StartFrame.INITIAL_WIDTH, StartFrame.INITIAL_HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setBackground(ServerGUIFactory.START_BACKGROUND);
    panel.setLayout(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

    GridBagConstraints c = new GridBagConstraints();
    c.weightx = 1;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    
    //title
    JLabel title = new JLabel("Welcome", SwingConstants.CENTER);
    title.setFont(Theme.getBoldFont(30));
    title.setForeground(ServerGUIFactory.START_TITLE);
    title.setAlignmentX(CENTER_ALIGNMENT);

    panel.add(ServerGUIFactory.getEmptyHeight(10), c);
    panel.add(title, c);
    panel.add(ServerGUIFactory.getEmptyHeight(60), c);

    //port number
    this.portField = new JTextField(StartFrame.DEFAULT_PORT, 20);
    this.portField.setFont(Theme.getPlainFont(22));
    this.portField.setMaximumSize(this.portField.getPreferredSize());
    this.portField.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    this.portField.setForeground(ServerGUIFactory.GENERAL_TEXT);
    this.portField.setCaretColor(ServerGUIFactory.GENERAL_TEXT);
    this.portField.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

    JLabel port = new JLabel("Port number");
    port.setFont(Theme.getPlainFont(18));
    port.setForeground(ServerGUIFactory.START_PORT_LABEL);

    panel.add(port, c);
    panel.add(ServerGUIFactory.getEmptyHeight(10), c);
    panel.add(this.portField, c);

    panel.add(ServerGUIFactory.getEmptyHeight(60), c);

    //error msg
    this.error = new JLabel();
    this.error.setAlignmentX(CENTER_ALIGNMENT);
    this.error.setFont(Theme.getPlainFont(15));
    this.error.setForeground(ServerGUIFactory.EMPHASIS_TEXT);
    this.error.setVisible(false);
    panel.add(this.error, c);
    
    //start button
    JButton button = ServerGUIFactory.getButton(
      "Host", 
      ServerGUIFactory.EMPHASIS_TEXT, 
      25, 
      10, 10, 
      ServerGUIFactory.EMPHASIS, 
      ServerGUIFactory.EMPHASIS_HOVER
    );

    button.addActionListener(this);
    panel.add(button, c);
    

    this.getContentPane().add(panel);
    this.getRootPane().setDefaultButton(button);
    this.setVisible(true);
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    String port = this.portField.getText();
    if (!port.matches("^[0-9]{1,5}$")) {
      this.error.setText("Invalid port");
      this.error.setVisible(true);
      return;
    }
    this.dispose();
    this.server.start();
  }
  
}
