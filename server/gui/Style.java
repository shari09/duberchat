package server.gui;

import java.awt.Font;

import javax.swing.Box;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;


public class Style {
  public final static Color BLUE = Style.getColour("4C69C7");
  public final static Color OVERLAY = Style.getColour("666666");
  public final static Color STRONGER_OVERLAY = Style.getColour("7F7F7F");
  public final static Color GRAY1 = Style.getColour("595959");
  public final static Color GRAY2 = Style.getColour("5A5A5A");
  public final static Color GRAY3 = Style.getColour("333333");
  public final static Color GRAY4 = Style.getColour("1F1F1F");
  public final static Color TEXT = Style.getColour("000000", 90);
  public final static Color DIM_TEXT = Style.getColour("000000", 80);
  public final static Color LIGHT_TEXT = Style.getColour("FFFFFF");
  public final static Color LIGHT_TEXT_OVERLAY = Style.getColour("FFFFFF", 60);



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

  public static Color getColour(Color color, int alpha) {
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
  
}
