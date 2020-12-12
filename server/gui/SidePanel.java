package server.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import server.services.GlobalServerServices;

/**
 * The main server window.
 * <p>
 * Created on 2020.12.11.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SidePanel extends JPanel implements ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final int iconSize = 35;
  private JButton log;
  private JButton users;

  public SidePanel() {
    super();
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.setAlignmentX(CENTER_ALIGNMENT);
    this.add(Style.getEmptyHeight(SidePanel.iconSize));
    this.log = this.getMenuItem("log-format");
    this.users = this.getMenuItem("user");
    this.add(this.users);
    this.add(this.log);
    this.setBackground(Style.GRAY3);
  }

  public JButton getMenuItem(String iconName) {
    JButton button = new JButton();
    try {
      BufferedImage img = ImageIO.read(new File("server/assets/"+iconName+".png"));
      Image icon = img.getScaledInstance(
        SidePanel.iconSize, 
        SidePanel.iconSize, 
        Image.SCALE_SMOOTH
      );
      button.setIcon(new ImageIcon(icon));
    } catch (Exception e) {
      System.out.println("Unable to add button icon");
      e.printStackTrace();
    }
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setForeground(Style.DIM_TEXT);
    button.setContentAreaFilled(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
    button.setFocusable(false);
    button.addActionListener(this);
    
    return button;
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.users) {
      System.out.println("users go brrr");
    } else if (e.getSource() == this.log) {
      System.out.println("log go brrr");
    }
  }
  
}
