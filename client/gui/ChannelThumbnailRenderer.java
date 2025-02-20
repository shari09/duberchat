package client.gui;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import client.services.ChannelServices;
import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.PrivateChannelMetadata;
import common.gui.Theme;

/**
 * Generates a panel containing the icon and relevant information of a channel.
 * <ul>
 * <li>If the channel is private, the other user's name and status will be displayed.
 * <li>If the channel is a group channel,
 *     the channel's name and number of participants will be displayed.
 * </ul>
 * <p>
 * Created on 2020.12.10.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChannelThumbnailRenderer implements ListCellRenderer<ChannelMetadata> {
  @Override
  public Component getListCellRendererComponent(
    JList<? extends ChannelMetadata> messages,
    ChannelMetadata metadata,
    int index,
    boolean isSelected,
    boolean hasFocus
  ) {
    JPanel panel = new JPanel();
    if (metadata instanceof PrivateChannelMetadata) {
      panel = ClientGUIFactory.getUserThumbnailPanel(
        ChannelServices.getOtherUserInPrivateChannel((PrivateChannelMetadata)metadata),
        Theme.getBoldFont(20),
        Theme.getItalicFont(15),
        ClientGUIFactory.BLUE_SHADE_3
      );

    } else if (metadata instanceof GroupChannelMetadata) {
      panel = ClientGUIFactory.getGroupChannelThumbnailPanel(
        (GroupChannelMetadata)metadata,
        Theme.getBoldFont(20),
        Theme.getItalicFont(15),
        ClientGUIFactory.BLUE_SHADE_3
      );
    }
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    if (isSelected) {
      panel.setBackground(ClientGUIFactory.GRAY_SHADE_1);
    }

    panel.setBorder(new EmptyBorder(10, 5, 10, 5));
    return panel;
  }
}