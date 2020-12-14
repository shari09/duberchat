package server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1;
    c.weighty = 0;

    panel.add(this.getSection("Username", this.user.getUsername()), c);
    panel.add(this.getSection("User ID", this.user.getUserId()), c);
    panel.add(this.getSection(
      "Creator notes", 
      "This doesn't listen to *all* real-time updates for now because I really don't have time to implement that."
    ), c);
    c.weighty = 1;
    panel.add(Box.createVerticalGlue(), c);
    this.add(panel);
  }


  public JPanel getSection(String labelMsg, String contentMsg) {
    JLabel label = new JLabel(labelMsg);
    label.setFont(ServerGUIFactory.getFont(15));
    label.setForeground(ServerGUIFactory.LIGHT_TEXT);    

    JTextArea content = new JTextArea(contentMsg);
    content.setLineWrap(true);
    content.setFont(ServerGUIFactory.getFont(20));
    content.setForeground(ServerGUIFactory.LIGHT_TEXT);
    content.setOpaque(false);
    content.setEditable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1;
    panel.add(label, c);
    panel.add(content, c);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
    panel.setBackground(Color.GRAY);

    return panel;
  }
}
