package client.gui;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Color;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import common.entities.UserMetadata;

/**
 * The frame to display the profile of a user.
 * <p>
 * Created on 2020.12.05.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserProfilePanel extends JPanel {
  private ImageIcon userIcon;
  // for the client
  public UserProfilePanel() {
    //this.userIcon = new ImageIcon("images/default_icon_user.png");
    this.add(new JLabel(GlobalClient.clientData.getUsername()));
  }

  // for a given user metadata
  public UserProfilePanel(UserMetadata userMetadata) {
  }

}
