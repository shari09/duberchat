package server.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicScrollBarUI;

import common.gui.Theme;
import server.entities.LogType;
import server.services.CommunicationService;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServerGUIFactory {

  // start frame
  public static final Color START_BACKGROUND = ServerGUIFactory.getColour("1D1D1D");

  public static final Color START_PORT_LABEL = ServerGUIFactory.getColour("A5A5A5");
  public static final Color START_TITLE = ServerGUIFactory.getColour("8B68E2");

  // the areas where the bg is purple and text is white
  public static final Color EMPHASIS_HOVER = ServerGUIFactory.getColour("7941CD");
  public static final Color EMPHASIS_TEXT = ServerGUIFactory.getColour("EEE6F9");
  public static final Color EMPHASIS = ServerGUIFactory.getColour("663AA8");

  // side panel
  public static final Color SIDE_PANEL = ServerGUIFactory.getColour("121212");

  // heading/title label
  public static final Color HEADING = ServerGUIFactory.getColour("1F1F1F");
  public static final Color HEADING_TEXT = ServerGUIFactory.getColour("E2E2E2");

  // entry panel
  public static final Color ENTRIES = ServerGUIFactory.getColour("1E1E1E");
  public static final Color ENTRY_ACTIVE = ServerGUIFactory.getColour("292929");
  public static final Color ENTRY_TEXT = ServerGUIFactory.getColour("A5A5A5");

  public static final Color ADMIN_BUTTON = ServerGUIFactory.getColour("181818");
  public static final Color ADMIN_BUTTON_HOVER = ServerGUIFactory.getColour("202020");
  public static final Color ADMIN_BUTTON_TEXT = ServerGUIFactory.getColour("8A67E1");

  public static final Color USER_SELECTION = ServerGUIFactory.getColour("232323");
  public static final Color USER_ACTIVE = ServerGUIFactory.getColour("2E2E2E");
  public static final Color USER_TEXT = ServerGUIFactory.getColour("5F81E4");

  public static final Color GENERAL_TEXT_BG = ServerGUIFactory.getColour("282828");
  public static final Color GENERAL_TEXT = ServerGUIFactory.getColour("E3E3E3");
  
  public static final Color SCROLL_BAR = ServerGUIFactory.getColour("474747");

  public static final Color LOG_SUCCESS = ServerGUIFactory.getColour("6EAF55");
  public static final Color LOG_CLIENT_ERROR = ServerGUIFactory.getColour("DED158");
  public static final Color LOG_SYSTEM_ERROR = ServerGUIFactory.getColour("D14A4A");
  public static final Color LOG_INFO = ServerGUIFactory.getColour("5270DA");



  public static Color getColour(String hex) {
    return Color.decode("#" + hex);
  }

  public static Component getEmptyHeight(int height) {
    return Box.createRigidArea(new Dimension(0, height));
  }

  public static Component getEmptyWidth(int width) {
    return Box.createRigidArea(new Dimension(width, 0));
  }

  public static JScrollPane getScrollPane(JPanel panel) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setBackground(ServerGUIFactory.HEADING);
    scrollPane.getVerticalScrollBar().setUI(ServerGUIFactory.getScrollbarUI());    
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

  public static BasicScrollBarUI getScrollbarUI() {
    return (new BasicScrollBarUI() {
      @Override
      public void configureScrollBarColors() {
        this.thumbColor = ServerGUIFactory.SCROLL_BAR;
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

  /**
   * 
   * @param title
   * @return
   */
  public static JPanel getHeader(String title) {
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Theme.getPlainFont(15));
    titleLabel.setForeground(ServerGUIFactory.HEADING_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(ServerGUIFactory.HEADING);
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
   * @param text
   * @param textColor
   * @param textSize
   * @param px
   * @param py
   * @param bg
   * @param hover
   * @param whiteIcon
   * @return
   */
  public static JCheckBox getCheckBox(
    String text, 
    Color textColor, 
    int textSize, 
    int px, int py, 
    Color bg, 
    Color hover,
    boolean whiteIcon
  ) {
    JCheckBox box = new JCheckBox(text);
    try {
      String path;
      if (whiteIcon) {
        path = "server/assets/unchecked_checkbox_white.png";
      } else {
        path = "server/assets/unchecked_checkbox.png";
      }
      BufferedImage img = ImageIO.read(new File(path));
      Image icon = img.getScaledInstance(textSize, textSize, Image.SCALE_SMOOTH);
      box.setIcon(new ImageIcon(icon));
      if (whiteIcon) {
        path = "server/assets/checked_checkbox_white.png";
      } else {
        path = "server/assets/checked_checkbox.png";
      }
      img = ImageIO.read(new File(path));
      icon = img.getScaledInstance(textSize, textSize, Image.SCALE_SMOOTH);
      box.setSelectedIcon(new ImageIcon(icon));
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Unable to load checkbox icon: %s \n%s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
    box.setFont(Theme.getPlainFont(textSize));
    box.setForeground(textColor);
    box.setCursor(new Cursor(Cursor.HAND_CURSOR));
    box.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    box.setMinimumSize(new Dimension(
      box.getPreferredSize().width + px * 2, 
      box.getPreferredSize().height + py * 2
    ));
    box.setFocusPainted(false);
    box.setHorizontalAlignment(SwingConstants.CENTER);
    box.setBackground(bg);
    box.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent evt) {
        box.setBackground(hover);
      }

      public void mouseExited(MouseEvent evt) {
        if (!box.getModel().isSelected()) {
          box.setBackground(bg);
        }
      }
    });
    box.addChangeListener(new ChangeListener(){
      public void stateChanged(ChangeEvent e) {
        if (box.getModel().isSelected()) {
          box.setBackground(hover);
        } else {
          box.setBackground(bg);
        }
      }
    });
    return box;

  }

  /**
   * 
   * @param text
   * @param textColor
   * @param textSize
   * @param px
   * @param py
   * @param bg
   * @param hover
   * @return
   */
  public static Button getButton(
    String text, 
    Color textColor, 
    int textSize, 
    int px, int py, 
    Color bg, 
    Color hover
  ) {
    Button button = new Button(text, bg, hover);
    button.setFont(Theme.getPlainFont(textSize));
    button.setForeground(textColor);
    button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));

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
      CommunicationService.log(String.format(
        "Unable to add button icon: %s \n%s", 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.SERVER_ERROR);
    }
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setContentAreaFilled(false);
    button.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    button.setFocusable(false);
    
    return button;
  }

}
