package client.gui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import common.entities.UserMetadata;
import common.gui.Theme;

/**
 * Generates a panel containing the metadata display of a user,
 * including username and status.
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserMetadataRenderer implements ListCellRenderer<UserMetadata> {
  @Override
  public Component getListCellRendererComponent(
    JList<? extends UserMetadata> participants,
    UserMetadata metadata,
    int index,
    boolean isSelected,
    boolean hasFocus
  ) {
    JPanel panel = ClientGUIFactory.getUserThumbnailPanel(
      metadata,
      Theme.getBoldFont(15),
      Theme.getItalicFont(10),
      ClientGUIFactory.BLUE_SHADE_3
    );
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
    panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    return panel;
  }
}