package client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import common.entities.ChannelMetadata;
import common.entities.GroupChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.gui.Theme;

/**
 * The factory used to generate client side GUI components.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang, Shari Sun
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

  public final static String USER_ICON_PATH = "client/assets/default_icon_user.png";
  public final static String GROUP_CHANNEL_ICON_PATH = "client/assets/default_icon_group_channel.png";
  public final static String SETTINGS_ICON_PATH = "client/assets/icon_settings.png";
  public final static String DIALOG_CONFIRMATION_ICON_PATH = "client/assets/dialog_icon_confirmation.png";
  public final static String DIALOG_INFORMATION_ICON_PATH = "client/assets/dialog_icon_information.png";
  public final static String DIALOG_ERROR_ICON_PATH = "client/assets/dialog_icon_error.png";
  public final static String DIALOG_SUCCESS_ICON_PATH = "client/assets/dialog_icon_success.png";
  public final static String DIALOG_BROADCAST_ICON_PATH = "client/assets/dialog_icon_broadcast.png";

  public static void initializeLookAndFeel() {
    UIManager.put("Panel.background", Color.WHITE);
    UIManager.put("OptionPane.background", Color.WHITE);
    UIManager.put("OptionPane.messageFont", Theme.getPlainFont(20));
    UIManager.put("OptionPane.messageForeground", ClientGUIFactory.PURPLE_SHADE_4);
    UIManager.put("Button.background", ClientGUIFactory.PURPLE_SHADE_1);
    UIManager.put("Button.foreground", ClientGUIFactory.PURPLE_SHADE_3);
    UIManager.put("OptionPane.buttonFont", Theme.getBoldFont(15));
    UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 10, 10, 10));
  }

  public static JScrollPane getScrollPane(JComponent panel) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setBackground(ClientGUIFactory.GRAY_SHADE_1);
    scrollPane.getVerticalScrollBar().setUI(ClientGUIFactory.getScrollbarUI());    
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

  public static BasicScrollBarUI getScrollbarUI() {
    return (new BasicScrollBarUI() {
      @Override
      public void configureScrollBarColors() {
        this.thumbColor = ClientGUIFactory.GRAY_SHADE_2;
      }

      @Override
      public JButton createDecreaseButton(int orientation) {
        JButton button = new JButton();
        button.setMaximumSize(new Dimension(0 ,0));
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        return button;
      }

      @Override
      public JButton createIncreaseButton(int orientation) {
        JButton button = new JButton();
        button.setMaximumSize(new Dimension(0 ,0));
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        return button;
      }
    });
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
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return button;
  }

  public static JButton getImageButton(ImageIcon icon) {
    JButton button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  public static JButton getTextButton(String text, Font font, Color textColor, Color bgColor, int hPad, int vPad) {
    JButton button = new JButton(text);
    button.setFont(font);
    button.setForeground(textColor);
    button.setBackground(bgColor);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setFocusPainted(false);
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
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  public static JTextField getTextField(int columns, String initialText, Font font, Color textColor, Color bgColor) {
    JTextField textField = ClientGUIFactory.getTextField(columns, font, textColor, bgColor);
    textField.setText(initialText);
    textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return textField;
  }

  public static JTextField getTextField(int columns, Font font, Color textColor, Color bgColor) {
    JTextField textField = new JTextField(columns);
    textField.setFont(font);
    textField.setForeground(textColor);
    textField.setBackground(bgColor);
    textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
    textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return textField;
  }

  public static JPasswordField getPasswordField(int columns, Font font, Color textColor, Color bgColor) {
    JPasswordField passField = new JPasswordField(columns);
    passField.setFont(font);
    passField.setForeground(textColor);
    passField.setBackground(bgColor);
    passField.setCursor(new Cursor(Cursor.HAND_CURSOR));
    passField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    return passField;
  }

  public static JTextArea getTextArea(String initialText, Font font, Color textColor, Color bgColor) {
    JTextArea textField = ClientGUIFactory.getTextArea(font, textColor, bgColor);
    textField.setText(initialText);
    return textField;
  }

  public static JTextArea getTextArea(Font font, Color textColor, Color bgColor) {
    JTextArea textField = new JTextArea();
    textField.setFont(font);
    textField.setForeground(textColor);
    textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
    textField.setBackground(bgColor);
    textField.setLineWrap(true);
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
    synchronized (metadata) {
      System.out.println("status of " + metadata.getUsername() + " is " + ClientGUIFactory.getStatusText(metadata.getStatus()));
      JPanel panel = new JPanel(new GridBagLayout());
      panel.setBackground(Color.WHITE);
      GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();
  
      JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(75, 75));
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
      System.out.println("this is the text being added to the panel: " + statusLabel.getText());
      constraints.gridy = 1;
      panel.add(statusLabel, constraints);
      
      return panel;
    }

  }

  public static JPanel getUserProfilePanel(
    UserMetadata metadata,
    Font nameFont,
    Font statusFont,
    Font descriptionFont,
    Color nameColor,
    Color descriptionColor
  ) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints constraints = ClientGUIFactory.getDefaultGridBagConstraints();

    JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(100, 100));
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
      nameColor
    );
    panel.add(nameLabel, constraints);

    JLabel statusLabel = ClientGUIFactory.getTextLabel(
      ClientGUIFactory.getStatusText(metadata.getStatus()),
      statusFont,
      ClientGUIFactory.getStatusColor(metadata.getStatus())
    );
    constraints.gridy = 1;
    panel.add(statusLabel, constraints);

    JTextArea description = ClientGUIFactory.getTextArea(
      metadata.getDescription(),
      descriptionFont,
      descriptionColor,
      Color.WHITE
    );
    description.setEditable(false);
    constraints.gridy = 2;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    panel.add(description, constraints);

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

    JLabel iconLabel = new JLabel(ClientGUIFactory.getGroupChannelIcon(75, 75));
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

    JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(75, 75));
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

    JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(30, 30));
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

    JLabel iconLabel = new JLabel(ClientGUIFactory.getUserIcon(30, 30));
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

  public static ImageIcon getIcon(String path, int width, int height) {
    ImageIcon icon = null;
    try {
      BufferedImage img = ImageIO.read(new File(path));
      Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      icon = new ImageIcon(resized);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return icon;
  }

  public static ImageIcon getUserIcon(int width, int height) {
    ImageIcon icon = null;
    try {
      BufferedImage img = ImageIO.read(new File(USER_ICON_PATH));
      Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      icon = new ImageIcon(resized);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return icon;
  }

  public static ImageIcon getGroupChannelIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(GROUP_CHANNEL_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static ImageIcon getSettingsIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(SETTINGS_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  
  public static ImageIcon getDialogConfirmationIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(DIALOG_CONFIRMATION_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static ImageIcon getDialogInformationIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(DIALOG_INFORMATION_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static ImageIcon getDialogErrorIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(DIALOG_ERROR_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static ImageIcon getDialogSuccessIcon(int width, int height) {
    Image img = null;
    ImageIcon icon = null;
    try {
      img = ImageIO.read(new File(DIALOG_SUCCESS_ICON_PATH));
      icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }
}