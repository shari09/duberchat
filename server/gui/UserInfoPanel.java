package server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import common.entities.UserMetadata;

/**
 * The main server window.
 * <p>
 * Created on 2020.12.13.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserInfoPanel extends JPanel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private UserMetadata user;

  public UserInfoPanel(UserMetadata user) {
    super();
    this.user = user;
    this.setLayout(new BorderLayout());

    this.add(
      ServerGUIFactory.getHeader(user.getUsername(), ServerGUIFactory.GRAY2),
      BorderLayout.PAGE_START
    );

    JLabel title = new JLabel("User ID");
    title.setFont(ServerGUIFactory.getFont(15));
    title.setForeground(ServerGUIFactory.LIGHT_TEXT);    

    JLabel userId = new JLabel(this.user.getUserId());
    userId.setFont(ServerGUIFactory.getFont(20));
    userId.setForeground(ServerGUIFactory.LIGHT_TEXT);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    panel.add(title, c);
    panel.add(userId, c);
    panel.setBackground(Color.GRAY);
    this.add(panel);

  }


  // public JPanel getUserIdPane() {
  //   JPanel panel = new JPanel();
  //   JLabel title = new JLabel("User ID");
  //   title.setFont(ServerGUIFactory.getFont(15));
  //   title.setForeground(ServerGUIFactory.DIM_TEXT);
  //   title.setBackground(Color.WHITE);

  //   JLabel userId = new JLabel(this.user.getUserId());
  //   userId.setFont(ServerGUIFactory.getFont(20));
  //   userId.setForeground(ServerGUIFactory.TEXT);
  //   userId.setBackground(Color.WHITE);

  //   GridBagConstraints c = new GridBagConstraints();
  //   c.gridx = 0;
  //   c.gridy = 0;
  //   c.weightx = 10;
  //   c.fill = GridBagConstraints.HORIZONTAL;
  //   panel.add(title, c);
  //   c.gridy = 1;
  //   panel.add(userId, c);
    
  //   c.gridy = 2;
  //   c.weighty = 50;
  //   panel.add(Box.createVerticalGlue(), c);

  //   return panel;
  // }
}
