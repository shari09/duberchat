package server.gui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Cursor;

/**
 * 
 * <p>
 * Created on 2020.12.14.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Button extends JButton {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private Color unpressed;
  private Color pressed;
  private boolean focused;

  public Button() {
    super();
  }

  public Button(String text, Color unpressed, Color pressed) {
    super(text);
    this.pressed = pressed;
    this.unpressed = unpressed;
    this.focused = false;

    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.setBackground(unpressed);
    this.setFocusPainted(false);
    this.setContentAreaFilled(false);
    
  }

  public void setFocused(boolean focused) {
    this.focused = focused;
  }

  @Override
  public void paintComponent(Graphics g) {
    if (this.getModel().isRollover() || this.focused) {
      g.setColor(this.pressed);
    } else {
      g.setColor(this.unpressed);
    }

    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    super.paintComponent(g);
  }
}
