package client.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JList;

import client.resources.GlobalClient;
import common.entities.UserMetadata;

public class FriendListMouseListener implements MouseListener {

  private JList<UserMetadata> friends;

  public FriendListMouseListener(JList<UserMetadata> friends) {
    this.friends = friends;
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    UserMetadata metadata = this.friends.getSelectedValue();
    if (metadata == null) {
      return;
    }

    if (SwingUtilities.isLeftMouseButton(e)) {
      this.showUserMetadataMessage(metadata);

    } else if (SwingUtilities.isRightMouseButton(e)) {
      this.promptFriendAction(metadata);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }
  @Override
  public void mouseClicked(MouseEvent e) {
  }
  @Override
  public void mouseEntered(MouseEvent e) {
  }
  @Override
  public void mouseExited(MouseEvent e) {
  }

  private synchronized void showUserMetadataMessage(UserMetadata metadata) {
    JOptionPane.showMessageDialog(
      null,
      metadata,
      "Friend Profile",
      JOptionPane.PLAIN_MESSAGE
    );
    GlobalClient.displayUserMetadata(metadata);
  }

  private synchronized void promptFriendAction(UserMetadata metadata) {
    GlobalClient.displayUserMetadata(metadata);
    // JOptionPane.showOptionDialog(
    //   null,
    //   message,
    //   title,
    //   optionType,
    //   JOptionPane.QUESTION_MESSAGE,
    //   null,
    //   options,
    //   null
    // );

  }

}
