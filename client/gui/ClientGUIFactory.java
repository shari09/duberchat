package client.gui;

import java.awt.Font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.ImageIcon;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import common.entities.GroupChannelMetadata;
import common.entities.ChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.gui.Theme;

/**
 * 
 * <p>
 * Created on 2020.12.13.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClientGUIFactory {
  // smaller number = lighter
  public final static Color GRAY_SHADE_1 = new Color(245, 245, 245);
  public final static Color GRAY_SHADE_2 = new Color(187, 187, 187);
  public final static Color GRAY_SHADE_3 = new Color(127, 127, 127);
  public final static Color GRAY_SHADE_4 = new Color( 51,  51,  51);

  public final static Color PURPLE_SHADE_1 = new Color(237, 232, 239);
  public final static Color PURPLE_SHADE_2 = new Color(196, 183, 216);
  public final static Color PURPLE_SHADE_3 = new Color(112,  86, 151);
  public final static Color PURPLE_SHADE_4 = new Color( 79,  59, 108);

  public final static Color YELLOW_SHADE_1 = new Color(234, 167,   0);
  public final static Color YELLOW_SHADE_2 = new Color(215, 125,  23);
  public final static Color YELLOW_SHADE_3 = new Color(133,  95,   0);

  public final static Color MAGENTA_SHADE_1 = new Color(187, 115, 157);
  public final static Color MAGENTA_SHADE_2 = new Color(142,  62,  157);

  public final static Color RED_SHADE_1 = new Color(238,  87,  88);
  public final static Color RED_SHADE_2 = new Color(170,  55,  50);

  public final static Color GREEN_SHADE_1 = new Color(229, 246, 212);
  public final static Color GREEN_SHADE_2 = new Color(154, 222, 209);
  public final static Color GREEN_SHADE_3 = new Color( 68, 140,  40);

  public final static Color BLUE_SHADE_1 = new Color(239, 248, 255);
  public final static Color BLUE_SHADE_2 = new Color(116, 190, 254);
  public final static Color BLUE_SHADE_3 = new Color( 76, 105, 199);
  public final static Color BLUE_SHADE_4 = new Color( 58,  79, 146);

  public final static String USER_ICON_PATH = "client/assets/2.png";
  public final static String GROUP_CHANNEL_ICON_PATH = "client/assets/1.png";
  public final static String SETTINGS_ICON_PATH = "client/assets/3.png";

  public static JScrollPane getScrollPane(Component component, boolean visibleScrollBar) {
    JScrollPane scrollPane = new JScrollPane(component);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    );
    scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    if (!visibleScrollBar) {
      scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    }
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);

    return scrollPane;
  }

  public static JTabbedPane getTabbedPane(Font font) {
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setBackground(ClientGUIFactory.BLUE_SHADE_1);
    tabbedPane.setForeground(ClientGUIFactory.BLUE_SHADE_4);
    tabbedPane.setFont(font);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return tabbedPane;
  }

  public static JLabel getTextLabel(String text, Font font, Color textColor) {
    JLabel label = new JLabel(text);
    label.setFont(font);
    label.setForeground(textColor);
    label.setOpaque(false);
    return label;
  }

  public static JButton getTextButton(String text, Font font, Color textColor, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(font);
    button.setForeground(textColor);
    button.setBackground(bgColor);
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return button;
  }

  public static JButton getImageButton(ImageIcon icon) {
    JButton button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    return button;
  }

  public static JButton getTextButton(String text, Font font, Color textColor, Color bgColor, int hPad, int vPad) {
    JButton button = new JButton(text);
    button.setFont(font);
    button.setForeground(textColor);
    button.setBackground(bgColor);
    button.setBorder(BorderFactory.createEmptyBorder(vPad, hPad, vPad, hPad));
    return button;
  }

  public static JButton getTextButton(String text, Font font, Color textColor, Color bgColor, int hPad, int vPad, Color borderColor) {
    JButton button = ClientGUIFactory.getTextButton(text, font, textColor, bgColor, hPad, vPad);
    button.setBorder(new LineBorder(borderColor, 3));
    return button;
  }

  public static JRadioButton getRadioButton(String text, Font font, Color textColor, int hPad, int vPad) {
    JRadioButton button = new JRadioButton(text);
    button.setFont(font);
    button.setForeground(textColor);
    button.setOpaque(false);
    button.setBorder(BorderFactory.createEmptyBorder(vPad, hPad, vPad, hPad));
    return button;
  }

  public static JTextField getTextField(int columns, String initialText, Font font, Color textColor, Color bgColor) {
    JTextField textField = ClientGUIFactory.getTextField(columns, font, textColor, bgColor);
    textField.setText(initialText);
    return textField;
  }

  public static JTextField getTextField(int columns, Font font, Color textColor, Color bgColor) {
    JTextField textField = new JTextField(columns);
    textField.setFont(font);
    textField.setForeground(textColor);
    textField.setBackground(bgColor);
    textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return textField;
  }

  public static JPasswordField getPasswordField(int columns, Font font, Color textColor, Color bgColor) {
    JPasswordField passField = new JPasswordField(columns);
    passField.setFont(font);
    passField.setForeground(textColor);
    passField.setBackground(bgColor);
    passField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return passField;
  }

  public static JTextArea getTextArea(int row, int columns, String initialText, Font font, Color textColor, Color bgColor) {
    JTextArea textField = ClientGUIFactory.getTextArea(row, columns, font, textColor, bgColor);
    textField.setText(initialText);
    return textField;
  }

  public static JTextArea getTextArea(int row, int columns, Font font, Color textColor, Color bgColor) {
    JTextArea textField = new JTextArea(row, columns);
    textField.setFont(font);
    textField.setForeground(textColor);
    textField.setBackground(bgColor);
    return textField;
  }

  public static GridBagConstraints getDefaultGridBagConstraints() {
    return ClientGUIFactory.getGridBagConstraints(
      0, 0, 1, 1,
      0.5, 0.5,
      10, 10, 
      GridBagConstraints.BOTH,
      GridBagConstraints.CENTER
    );
  }
  public static GridBagConstraints getGridBagConstraints(
    int gridx, int gridy, int gridw, int gridh,
    double weightx, double weighty,
    int xPad, int yPad,
    int fill,
    int anchor
  ) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.gridx = gridx;
    constraints.gridy = gridx;
    constraints.gridwidth= gridw;
    constraints.gridheight = gridh;
    constraints.ipadx = xPad;
    constraints.ipady = yPad;
    constraints.fill = fill;
    constraints.anchor = anchor;
    return constraints;
  }

  public static JPanel getUserThumbnailPanel(
    UserMetadata metadata,
    Font nameFont,
    Font statusFont,
    Color textColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(new ImageIcon(ClientGUIFactory.getUserIcon()));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    JLabel nameLabel = ClientGUIFactory.getTextLabel(
      metadata.getUsername(),
      nameFont,
      textColor
    );
    panel.add(nameLabel, constraints);

    JLabel statusLabel = ClientGUIFactory.getTextLabel(
      ClientGUIFactory.getStatusText(metadata.getStatus()),
      statusFont,
      ClientGUIFactory.getStatusColor(metadata.getStatus())
    );
    constraints.gridy = 1;
    panel.add(statusLabel, constraints);
    
    return panel;
  }

  public static JPanel getGroupChannelThumbnailPanel(
    GroupChannelMetadata metadata,
    Font titleFont,
    Font numParticipantsFont,
    Color textColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(new ImageIcon(ClientGUIFactory.getGroupChannelIcon()));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    JLabel nameLabel = ClientGUIFactory.getTextLabel(
      metadata.getChannelName(),
      titleFont,
      textColor
    );
    panel.add(nameLabel, constraints);

    JLabel partLabel = ClientGUIFactory.getTextLabel(
      metadata.getParticipants().size() + " participants",
      numParticipantsFont,
      textColor
    );
    constraints.gridy = 1;
    panel.add(partLabel, constraints);

    return panel;
  }

  public static JPanel getParticipantThumbnailPanel(
    ChannelMetadata metadata,
    UserMetadata participant,
    Font font,
    Color textColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(new ImageIcon(ClientGUIFactory.getUserIcon()));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    String nameLabelText = participant.getUsername();
    if (
      (metadata instanceof GroupChannelMetadata)
      && (((GroupChannelMetadata)metadata).getOwnerId().equals(participant.getUserId()))
    ) {
      nameLabelText += " (Owner)";
    }
    JLabel nameLabel = ClientGUIFactory.getTextLabel(
      nameLabelText,
      font,
      textColor
    );
    panel.add(nameLabel, constraints);

    JLabel statusLabel = ClientGUIFactory.getTextLabel(
      ClientGUIFactory.getStatusText(participant.getStatus()),
      font,
      ClientGUIFactory.getStatusColor(participant.getStatus())
    );
    constraints.gridy = 1;
    panel.add(statusLabel, constraints);
    
    
    return panel;
  }

  public static JPanel getIncomingFriendRequestPanel(
    UserMetadata metadata,
    String requestMsg,
    Font usernameFont,
    Font requestMsgFont,
    Color textColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(new ImageIcon(ClientGUIFactory.getUserIcon()));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    JLabel nameLabel = ClientGUIFactory.getTextLabel(
      metadata.getUsername(),
      usernameFont,
      textColor
    );
    panel.add(nameLabel, constraints);

    JLabel partLabel = ClientGUIFactory.getTextLabel(
      requestMsg,
      requestMsgFont,
      textColor
    );
    constraints.gridy = 1;
    panel.add(partLabel, constraints);

    return panel;
  }

  public static JPanel getOutgoingFriendRequestPanel(
    UserMetadata recipientMetadata,
    Font usernameFont,
    Font statusFont,
    Color textColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(new ImageIcon(ClientGUIFactory.getUserIcon()));
    constraints.weightx = 0.3;
    constraints.weighty = 0;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    constraints.ipadx = 2;
    panel.add(iconLabel, constraints);

    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.ipadx = 0;
    constraints.gridx = 2;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    JLabel nameLabel = ClientGUIFactory.getTextLabel(
      recipientMetadata.getUsername(),
      usernameFont,
      textColor
    );
    panel.add(nameLabel, constraints);

    JLabel partLabel = ClientGUIFactory.getTextLabel(
      "pending",
      statusFont,
      textColor
    );
    constraints.gridy = 1;
    panel.add(partLabel, constraints);

    return panel;
  }

  public static Color getStatusColor(UserStatus status) {
    switch (status) {
      case ACTIVE:
        return ClientGUIFactory.GREEN_SHADE_3;
      case IDLE:
        return ClientGUIFactory.YELLOW_SHADE_2;
      case DO_NOT_DISTURB:
        return ClientGUIFactory.RED_SHADE_1;
      case OFFLINE:
        return ClientGUIFactory.GRAY_SHADE_3;
    }
    return ClientGUIFactory.GRAY_SHADE_4;
  }

  public static String getStatusText(UserStatus status) {
    switch (status) {
      case ACTIVE:
        return "Online";
      case IDLE:
        return "Idle";
      case DO_NOT_DISTURB:
        return "Do not disturb";
      case OFFLINE:
        return "Offline";
    }
    return "";
  }

  public static BufferedImage getUserIcon() {
    BufferedImage icon = null;
    try {
      icon = ImageIO.read(new File(USER_ICON_PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static BufferedImage getGroupChannelIcon() {
    BufferedImage icon = null;
    try {
      icon = ImageIO.read(new File(GROUP_CHANNEL_ICON_PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static BufferedImage getSettingsIcon() {
    BufferedImage icon = null;
    try {
      icon = ImageIO.read(new File(SETTINGS_ICON_PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  // public static JButton getIconButton(
  //   String iconName,
  //   int iconSize,
  //   int px,
  //   int py
  // ) {
  //   JButton button = new JButton();
  //   try {
  //     BufferedImage img = ImageIO.read(new File("server/assets/"+iconName+".png"));
  //     Image icon = img.getScaledInstance(
  //       iconSize, iconSize,
  //       Image.SCALE_SMOOTH
  //     );
  //     button.setIcon(new ImageIcon(icon));
  //   } catch (Exception e) {
  //     System.out.println("Unable to add button icon");
  //     e.printStackTrace();
  //   }
  //   button.setCursor(new Cursor(Cursor.HAND_CURSOR));
  //   button.setContentAreaFilled(false);
  //   button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
  //   button.setFocusable(false);
    
  //   return button;
  // }

}
