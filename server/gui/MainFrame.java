package server.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.11.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainFrame extends JFrame implements ActionListener {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;

  public MainFrame() {
    super("DuberChat Server");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
    this.setResizable(false);
    this.setBackground(Color.BLACK);

    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    SidePanel sidePanel = new SidePanel();
    

    this.getContentPane().add(sidePanel);
    this.getContentPane().add(Style.getEmptyWidth(50));
    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

  }
  
}
