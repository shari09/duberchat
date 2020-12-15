package client.gui;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import common.gui.Theme;
import common.entities.UserMetadata;

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
    LineBorder border = new LineBorder(ClientGUIFactory.GRAY_SHADE_3, 1);
    panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 15)));
    panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    return panel;
  }
}