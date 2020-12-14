package server.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import server.Server;
import server.entities.EventType;
import server.services.GlobalServices;

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
  private Server socket;

  public StartFrame(Server socket) {
    super("Duberchat Server");
    this.socket = socket;
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(StartFrame.INITIAL_WIDTH, StartFrame.INITIAL_HEIGHT);
    this.setResizable(false);

    JPanel panel = new JPanel();
    panel.setBackground(Color.WHITE);
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);
    
    //title
    JLabel title = new JLabel(
      "<html><div style='text-align: center;'>DuberChat<br/>Server</div></html>",
      SwingConstants.CENTER
    );
    title.setFont(ServerGUIFactory.getFont(30));
    title.setForeground(ServerGUIFactory.BLUE);
    title.setAlignmentX(CENTER_ALIGNMENT);

    panel.add(ServerGUIFactory.getEmptyHeight(25));
    panel.add(title);
    panel.add(ServerGUIFactory.getEmptyHeight(60));

    //port number
    this.portField = new JTextField(StartFrame.DEFAULT_PORT, 20);
    this.portField.setFont(ServerGUIFactory.getFont(15));
    this.portField.setMaximumSize(this.portField.getPreferredSize());
    this.portField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    
    JLabel port = new JLabel("PORT");
    port.setAlignmentX(CENTER_ALIGNMENT);
    port.setFont(ServerGUIFactory.getFont(13));

    panel.add(port);
    panel.add(ServerGUIFactory.getEmptyHeight(10));
    panel.add(this.portField);

    panel.add(ServerGUIFactory.getEmptyHeight(60));

    //error msg
    this.error = new JLabel("");
    this.error.setAlignmentX(CENTER_ALIGNMENT);
    this.error.setFont(ServerGUIFactory.getFont(13));
    this.error.setForeground(Color.RED);
    
    //start button
    JButton button = new JButton("START");
    button.setFont(ServerGUIFactory.getFont(20));
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.setForeground(ServerGUIFactory.BLUE);
    button.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(ServerGUIFactory.BLUE, 1),
      BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setRolloverEnabled(false);
    button.setFocusable(false);
    button.setContentAreaFilled(false);
    button.addActionListener(this);

    panel.add(button);
    

    this.getContentPane().add(panel);
    this.getRootPane().setDefaultButton(button);
    this.setVisible(true);
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    String port = this.portField.getText();
    if (!port.matches("^[0-9]{1,5}$")) {
      this.error.setText("Invalid port");
      return;
    }
    this.dispose();
    GlobalServices.serverEventQueue.emitEvent(
      EventType.NEW_LOG, 
      1,
      "Server starting..."
    );
    this.socket.start();

  }
  
}
