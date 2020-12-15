package client.gui;

import java.awt.Font;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Box;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagConstraints;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

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

  public final static Color BLUE_SHADE_1 = new Color(154, 205, 250);
  public final static Color BLUE_SHADE_2 = new Color(117, 191, 254);
  public final static Color BLUE_SHADE_3 = new Color( 76, 105, 199);

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
    return textField;
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

  // public static GridBagConstraints getScrollConstraints() {
  //   GridBagConstraints c = new GridBagConstraints();
  //   c.fill = GridBagConstraints.HORIZONTAL;
  //   c.anchor = GridBagConstraints.NORTH;
  //   c.weightx = 1;
  //   c.weighty = 1;
  //   c.gridx = 0;
  //   return c;
  // }

  // /**
  //  * 
  //  * @param title
  //  * @param px         padding x
  //  * @param py         padding y
  //  * @return
  //  */
  // public static JCheckBox getCheckBox(
  //   String title, 
  //   int px, int py, 
  //   Color bg,
  //   Color hover,
  //   boolean whiteIcon
  // ) {
  //   JCheckBox box = new JCheckBox(title);
  //   try {
  //     String path;
  //     if (whiteIcon) {
  //       path = "server/assets/unchecked_checkbox_white.png";
  //     } else {
  //       path = "server/assets/unchecked_checkbox.png";
  //     }
  //     BufferedImage img = ImageIO.read(new File(path));
  //     Image icon = img.getScaledInstance(
  //       20, 20, 
  //       Image.SCALE_SMOOTH
  //     );
  //     box.setIcon(new ImageIcon(icon));
  //     if (whiteIcon) {
  //       path = "server/assets/checked_checkbox_white.png";
  //     } else {
  //       path = "server/assets/checked_checkbox.png";
  //     }
  //     img = ImageIO.read(new File(path));
  //     icon = img.getScaledInstance(
  //       20, 20, 
  //       Image.SCALE_SMOOTH
  //     );
  //     box.setSelectedIcon(new ImageIcon(icon));
  //   } catch (Exception e) {
  //     System.out.println("Unable to add button icon");
  //     e.printStackTrace();
  //   }
  //   box.setFont(ServerGUIFactory.getFont(17));
  //   box.setForeground(ServerGUIFactory.TEXT);
  //   box.setCursor(new Cursor(Cursor.HAND_CURSOR));
  //   box.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
  //   box.setMinimumSize(new Dimension(
  //     box.getPreferredSize().width+px*2,
  //     box.getPreferredSize().height+py*2
  //   ));
  //   box.setFocusPainted(false);
  //   box.setHorizontalAlignment(SwingConstants.CENTER);
  //   box.setBackground(bg);
  //   box.addMouseListener(new MouseAdapter() {
  //     public void mouseEntered(MouseEvent evt) {
  //       box.setBackground(hover);
  //     }

  //     public void mouseExited(MouseEvent evt) {
  //       box.setBackground(bg);
  //     }
  //   });
  //   return box;

  // }


  // public static JButton getButton(
  //   String text, 
  //   Color textColor,
  //   int textSize,
  //   int px, 
  //   int py,
  //   Color bg,
  //   Color hover
  // ) {
  //   JButton button = new JButton(text);
  //   button.setCursor(new Cursor(Cursor.HAND_CURSOR));
  //   button.setFont(ServerGUIFactory.getFont(textSize));
  //   button.setForeground(textColor);
  //   button.setBackground(bg);
  //   button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
  //   button.setFocusPainted(false);
  //   button.addMouseListener(new MouseAdapter() {
  //     public void mouseEntered(MouseEvent evt) {
  //       button.setBackground(hover);
  //     }

  //     public void mouseExited(MouseEvent evt) {
  //       button.setBackground(bg);
  //     }
  //   });

  //   return button;
  // }



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
