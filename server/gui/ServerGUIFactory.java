package server.gui;

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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServerGUIFactory {
  public final static Color BLUE = ServerGUIFactory.getColour("4C69C7");
  public final static Color OVERLAY = ServerGUIFactory.getColour("666666");
  public final static Color STRONGER_OVERLAY = ServerGUIFactory.getColour("7F7F7F");
  public final static Color GRAY1 = ServerGUIFactory.getColour("595959");
  public final static Color GRAY2 = ServerGUIFactory.getColour("5A5A5A");
  public final static Color GRAY3 = ServerGUIFactory.getColour("333333");
  public final static Color GRAY4 = ServerGUIFactory.getColour("1F1F1F");
  public final static Color TEXT = ServerGUIFactory.getColour("1A1A1A");
  public final static Color DIM_TEXT = ServerGUIFactory.getColour("333333");
  public final static Color LIGHT_TEXT = ServerGUIFactory.getColour("FFFFFF");
  public final static Color LIGHT_TEXT_OVERLAY = ServerGUIFactory.getColour("FFFFFF", 60);
  public final static Color LIGHT_PURPLE = ServerGUIFactory.getColour("EDE8EF");
  public final static Color LIGHT_PURPLE2 = ServerGUIFactory.getColour("BCB3D2");
  public final static Color DARK_PURPLE = ServerGUIFactory.getColour("4E3D6A");
  public final static Color DARK_PURPLE_OVERLAY = ServerGUIFactory.getColour("705697");

  public static Font getFont(int size) {
    return new Font("Arial", Font.PLAIN, size);
  }
  public static Color getColour(String hex, int alpha) {
    Color color = Color.decode("#"+hex);
    return new Color(
      color.getRed(),
      color.getGreen(),
      color.getBlue(),
      (int)((100-alpha)/100.0*255)
    );
  }

  public static Color getColour(String hex) {
    return Color.decode("#"+hex);
  }

  public static Component getEmptyHeight(int height) {
    return Box.createRigidArea(new Dimension(0, height));
  }

  public static Component getEmptyWidth(int width) {
    return Box.createRigidArea(new Dimension(width, 0));
  }
  
  public static JScrollPane getScrollPane(JPanel panel, boolean visibleScrollBar) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
    );
    scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    if (!visibleScrollBar) {
      scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    }
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

  public static JPanel getHeader(String title, Color bg) {
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(ServerGUIFactory.getFont(15));
    titleLabel.setForeground(ServerGUIFactory.LIGHT_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(bg);
    titlePanel.setPreferredSize(titlePanel.getPreferredSize());
    return titlePanel;
  }

  public static GridBagConstraints getScrollConstraints() {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    return c;
  }

  /**
   * 
   * @param title
   * @param px         padding x
   * @param py         padding y
   * @return
   */
  public static JCheckBox getCheckBox(
    String title, 
    int px, int py, 
    Color bg,
    Color hover,
    boolean whiteIcon
  ) {
    JCheckBox box = new JCheckBox(title);
    try {
      String path;
      if (whiteIcon) {
        path = "server/assets/unchecked_checkbox_white.png";
      } else {
        path = "server/assets/unchecked_checkbox.png";
      }
      BufferedImage img = ImageIO.read(new File(path));
      Image icon = img.getScaledInstance(
        20, 20, 
        Image.SCALE_SMOOTH
      );
      box.setIcon(new ImageIcon(icon));
      if (whiteIcon) {
        path = "server/assets/checked_checkbox_white.png";
      } else {
        path = "server/assets/checked_checkbox.png";
      }
      img = ImageIO.read(new File(path));
      icon = img.getScaledInstance(
        20, 20, 
        Image.SCALE_SMOOTH
      );
      box.setSelectedIcon(new ImageIcon(icon));
    } catch (Exception e) {
      System.out.println("Unable to add button icon");
      e.printStackTrace();
    }
    box.setFont(ServerGUIFactory.getFont(17));
    box.setForeground(ServerGUIFactory.TEXT);
    box.setCursor(new Cursor(Cursor.HAND_CURSOR));
    box.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    box.setMinimumSize(new Dimension(
      box.getPreferredSize().width+px*2,
      box.getPreferredSize().height+py*2
    ));
    box.setFocusPainted(false);
    box.setHorizontalAlignment(SwingConstants.CENTER);
    box.setBackground(bg);
    box.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent evt) {
        box.setBackground(hover);
      }

      public void mouseExited(MouseEvent evt) {
        box.setBackground(bg);
      }
    });
    return box;

  }


  public static JButton getButton(
    String text, 
    Color textColor,
    int textSize,
    int px, 
    int py,
    Color bg,
    Color hover
  ) {
    JButton button = new JButton(text);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setFont(ServerGUIFactory.getFont(textSize));
    button.setForeground(textColor);
    button.setBackground(bg);
    button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    button.setFocusPainted(false);
    button.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent evt) {
        button.setBackground(hover);
      }

      public void mouseExited(MouseEvent evt) {
        button.setBackground(bg);
      }
    });

    return button;
  }



  public static JButton getIconButton(
    String iconName,
    int iconSize,
    int px,
    int py
  ) {
    JButton button = new JButton();
    try {
      BufferedImage img = ImageIO.read(new File("server/assets/"+iconName+".png"));
      Image icon = img.getScaledInstance(
        iconSize, iconSize,
        Image.SCALE_SMOOTH
      );
      button.setIcon(new ImageIcon(icon));
    } catch (Exception e) {
      System.out.println("Unable to add button icon");
      e.printStackTrace();
    }
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setContentAreaFilled(false);
    button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    button.setFocusable(false);
    
    return button;
  }

}
